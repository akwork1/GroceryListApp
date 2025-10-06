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
    indices = [Index("itemId"), Index("dateKey")]
)
data class IntakeEstimate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val dateKey: String,
    val estimatedGrams: Int,
    val calories: Int?,
    val protein: Int?,
    val carbs: Int?,
    val fat: Int?
)
