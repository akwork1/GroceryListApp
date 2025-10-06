package com.atticus.grocerylistapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.atticus.grocerylistapp.data.entity.SessionEvent

data class EventWithName(
    val id: Long,
    val itemId: Long,
    val type: String,
    val happenedAt: Long,
    val priceTotal: Double?,
    val displayName: String
)

@Dao
interface SessionEventDao {
    @Insert
    fun insert(e: SessionEvent): Long

    @Query("""
        SELECT e.id, e.itemId, e.type, e.happenedAt, e.priceTotal, p.displayName AS displayName
        FROM SessionEvent e
        JOIN PantryItem p ON p.id = e.itemId
        WHERE e.happenedAt BETWEEN :from AND :to
        ORDER BY e.happenedAt DESC
    """)
    fun eventsInRange(from: Long, to: Long): List<EventWithName>
}
