package com.atticus.grocerylistapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = PantryItem::class,
        parentColumns = ["id"], childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("itemId"), Index("plannedAt")]
)
data class PlannedLine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val quantity: Double = 1.0,
    val plannedAt: Long = System.currentTimeMillis(),
    val dueBy: Long? = null,
    val source: String = "manual",
    val status: String = "active"
)
