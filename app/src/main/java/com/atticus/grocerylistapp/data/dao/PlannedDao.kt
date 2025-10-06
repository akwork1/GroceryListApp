package com.atticus.grocerylistapp.data.dao

import androidx.room.*
import com.atticus.grocerylistapp.data.entity.PlannedLine

data class PlannedWithItemName(
    val id: Long,
    val itemId: Long,
    val quantity: Double,
    val plannedAt: Long,
    val dueBy: Long?,
    val source: String,
    val status: String,
    val name: String
)

@Dao
interface PlannedDao {
    @Insert fun insert(line: PlannedLine): Long
    @Update fun update(line: PlannedLine)
    @Delete fun delete(line: PlannedLine)

    @Query("SELECT * FROM PlannedLine WHERE id=:id")
    fun byId(id: Long): PlannedLine?

    // List screen ordering: active (top), then bought, then oos
    @Query("""
        SELECT l.id, l.itemId, l.quantity, l.plannedAt, l.dueBy, l.source, l.status,
               p.displayName AS name
        FROM PlannedLine l
        JOIN PantryItem p ON p.id = l.itemId
        WHERE l.status IN ('active','bought','oos')
        ORDER BY CASE l.status
            WHEN 'active' THEN 0
            WHEN 'bought' THEN 1
            WHEN 'oos' THEN 2
            ELSE 3 END, l.plannedAt DESC
    """)
    fun currentForDisplay(): List<PlannedWithItemName>

    // For Done snapshot
    @Query("""
        SELECT l.id, l.itemId, l.quantity, l.plannedAt, l.dueBy, l.source, l.status,
               p.displayName AS name
        FROM PlannedLine l
        JOIN PantryItem p ON p.id = l.itemId
        WHERE l.status IN ('bought','oos')
        ORDER BY l.plannedAt DESC
    """)
    fun completedForSnapshot(): List<PlannedWithItemName>

    @Query("UPDATE PlannedLine SET status=:status WHERE id=:id")
    fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM PlannedLine WHERE status IN ('bought','oos')")
    fun deleteCompleted()
}
