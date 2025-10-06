package com.atticus.grocerylistapp.repo

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.atticus.grocerylistapp.data.db.AppDb
import com.atticus.grocerylistapp.data.entity.PantryItem
import com.atticus.grocerylistapp.data.entity.PlannedLine
import com.atticus.grocerylistapp.data.entity.Purchase
import com.atticus.grocerylistapp.ocr.ReceiptParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import com.atticus.grocerylistapp.data.entity.SessionEvent


val Context.dataStore by preferencesDataStore("settings")

class GroceryRepo(private val db: AppDb, private val ctx: Context) {
    enum class Frequency(val days: Int) { TWICE_WEEK(3), WEEKLY(7), FORTNIGHT(14) }
    private val FREQ_KEY = intPreferencesKey("freq_days")

    suspend fun setFrequency(f: Frequency) { ctx.dataStore.edit { it[FREQ_KEY] = f.days } }

    suspend fun addManual(query: String) = withContext(Dispatchers.IO) {
        val norm = normalize(query)
        val existing = db.pantry().byNorm(norm)
        val id = existing?.id ?: db.pantry().insert(PantryItem(displayName = query, normalizedName = norm))
        db.planned().insert(PlannedLine(itemId = id, source = "manual"))
    }

    suspend fun addPlanned(itemId: Long) = withContext(Dispatchers.IO) {
        db.planned().insert(PlannedLine(itemId = itemId, source = "recommendation"))
    }

    suspend fun removePlanned(plannedId: Long) = withContext(Dispatchers.IO) {
        db.planned().byId(plannedId)?.let { db.planned().delete(it) }
    }

    suspend fun markBought(plannedId: Long) = withContext(Dispatchers.IO) {
        db.planned().updateStatus(plannedId, "bought")
    }

    suspend fun markOutOfStock(plannedId: Long) = withContext(Dispatchers.IO) {
        db.planned().updateStatus(plannedId, "oos")
    }

    suspend fun clearCompleted() = withContext(Dispatchers.IO) {
        val doneAt = System.currentTimeMillis()
        val finished = db.planned().completedForSnapshot()

        // Write one event per completed line (bought or oos)
        finished.forEach { line ->
            db.sessionEvent().insert(
                SessionEvent(
                    itemId = line.itemId,
                    type = line.status,      // "bought" or "oos"
                    happenedAt = doneAt,
                    priceTotal = null        // can be filled later by receipt ingestion
                )
            )
        }

        // Remove from the working list
        db.planned().deleteCompleted()
    }

    data class HistoryEntry(
        val itemId: Long,
        val name: String,
        val type: String,           // "bought" | "oos"
        val priceTotal: Double?,    // present for bought if known
        val happenedAt: Long
    )

    suspend fun historyEntriesInDay(dayStart: Long, dayEnd: Long): List<HistoryEntry> =
        withContext(Dispatchers.IO) {
            db.sessionEvent().eventsInRange(dayStart, dayEnd).map {
                HistoryEntry(itemId = it.itemId, name = it.displayName, type = it.type, priceTotal = it.priceTotal, happenedAt = it.happenedAt)
            }
        }


    suspend fun rollover(plannedId: Long, days: Int = 7) = withContext(Dispatchers.IO) {
        val line = db.planned().byId(plannedId) ?: return@withContext
        db.planned().update(line.copy(status = "rolled"))
        db.planned().insert(PlannedLine(itemId = line.itemId, source = "manual", dueBy = (System.currentTimeMillis() + days*86_400_000L)))
    }

    suspend fun currentList() = withContext(Dispatchers.IO) {
        db.planned().currentForDisplay()
    }

    data class Recommendation(val itemId: Long, val name: String, val score: Double, val avgPrice: Double?, val lastPrice: Double?, val isSaleNow: Boolean)

    suspend fun computeRecommendations(limit: Int = 20): List<Recommendation> = withContext(Dispatchers.IO) {
        val all = db.pantry().search("")
        val now = System.currentTimeMillis()
        all.map { item ->
            val recency = recentness(item.id, now)
            val cadence = cadenceUrgency(item.id, now)
            val likeBoost = if (item.liked == true) 1.0 else 0.0
            val dislike = if (item.disliked == true) 1.5 else 0.0
            val score = 0.5*recency + 0.9*cadence + 0.8*likeBoost - 1.2*dislike
            val avg = db.purchase().avgUnitPrice(item.id)
            val last = db.purchase().lastUnitPrice(item.id)
            Recommendation(item.id, item.displayName, score, avg, last, isSale(avg, last))
        }.sortedByDescending { it.score }.take(limit)
    }

    private fun normalize(s: String) =
        s.lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()


    private fun recentness(itemId: Long, now: Long): Double {
        val lastDates = db.purchase().lastNPurchaseDates(itemId, 1)
        if (lastDates.isEmpty()) return 0.2
        val days = (now - lastDates.first()) / 86_400_000.0
        return 1 - (1 / (1 + exp(-0.1 * (days - 5))))
    }

    private fun cadenceUrgency(itemId: Long, now: Long): Double {
        val lastDates = db.purchase().lastNPurchaseDates(itemId, 5)
        if (lastDates.size < 2) return 0.1
        val deltas = lastDates.zipWithNext { a, b -> kotlin.math.abs(a - b).toDouble() }
        if (deltas.isEmpty()) return 0.1
        val median = deltas.sorted()[deltas.size/2] / 86_400_000.0
        val last = lastDates.first()
        val daysSince = (now - last) / 86_400_000.0
        return min(1.0, max(0.0, (daysSince - median*0.6) / (median*0.6)))
    }

    private fun isSale(avg: Double?, last: Double?): Boolean = (avg != null && last != null && last < avg * 0.85)

    suspend fun ingestReceiptLines(lines: List<String>, store: String? = null) = withContext(Dispatchers.IO) {
        val parsed = ReceiptParser.parse(lines)
        parsed.forEach { line ->
            val existing = db.pantry().byNorm(line.norm)
            val id = existing?.id ?: db.pantry().insert(PantryItem(displayName = line.name, normalizedName = line.norm, sizeGrams = line.sizeGrams))
            val itemId = existing?.id ?: id
            db.purchase().insert(Purchase(itemId = itemId, quantity = 1.0, unitPrice = line.unitPrice, totalPrice = line.unitPrice, store = store))
        }
    }

    data class SpendSummary(val total: Double, val byItem: List<Pair<String, Double>>)
    suspend fun spendBetween(fromMs: Long, toMs: Long): SpendSummary = withContext(Dispatchers.IO) {
        val list = db.purchase().purchasesInRange(fromMs, toMs)
        val total = list.sumOf { it.totalPrice }
        val byItem = list.groupBy { it.displayName }.mapValues { e -> e.value.sumOf { it.totalPrice } }
            .toList().sortedByDescending { it.second }
        SpendSummary(total, byItem)
    }

    suspend fun searchPantryDisplayNames(prefix: String): List<String> =
        withContext(Dispatchers.IO) {
            db.pantry().search(normalize(prefix)).map { it.displayName }
        }

    suspend fun purchasesWithNamesInRange(fromMs: Long, toMs: Long) =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            db.purchase().purchasesInRange(fromMs, toMs)
        }

}
