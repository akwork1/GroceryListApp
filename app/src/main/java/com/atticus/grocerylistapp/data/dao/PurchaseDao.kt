package com.atticus.grocerylistapp.data.dao

import androidx.room.*
import com.atticus.grocerylistapp.data.entity.Purchase

data class PurchaseWithItem(
    val id: Long,
    val itemId: Long,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double,
    val store: String?,
    val purchasedAt: Long,
    val displayName: String
)

@Dao
interface PurchaseDao {
    @Insert fun insert(p: Purchase): Long

    @Query("""
        SELECT p.id, p.itemId, p.quantity, p.unitPrice, p.totalPrice, p.store, p.purchasedAt, i.displayName
        FROM Purchase p
        JOIN PantryItem i ON i.id = p.itemId
        WHERE p.purchasedAt BETWEEN :from AND :to
        ORDER BY p.purchasedAt DESC
    """)
    fun purchasesInRange(from: Long, to: Long): List<PurchaseWithItem>

    @Query("SELECT AVG(unitPrice) FROM Purchase WHERE itemId=:itemId")
    fun avgUnitPrice(itemId: Long): Double?

    @Query("SELECT unitPrice FROM Purchase WHERE itemId=:itemId ORDER BY purchasedAt DESC LIMIT 1")
    fun lastUnitPrice(itemId: Long): Double?

    @Query("SELECT purchasedAt FROM Purchase WHERE itemId=:itemId ORDER BY purchasedAt DESC LIMIT :n")
    fun lastNPurchaseDates(itemId: Long, n: Int): List<Long>

    @Query("SELECT * FROM Purchase WHERE itemId=:itemId ORDER BY purchasedAt ASC")
    fun allForItem(itemId: Long): List<Purchase>
}
