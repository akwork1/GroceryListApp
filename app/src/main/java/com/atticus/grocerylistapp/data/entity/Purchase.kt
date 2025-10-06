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
    indices = [Index("itemId"), Index("purchasedAt")]
)
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val quantity: Double = 1.0,
    val unitPrice: Double,
    val totalPrice: Double,
    val store: String? = null,
    val purchasedAt: Long = System.currentTimeMillis(),
    val fromReceiptId: Long? = null
)
