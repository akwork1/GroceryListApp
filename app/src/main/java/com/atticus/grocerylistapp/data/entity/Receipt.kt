package com.atticus.grocerylistapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Receipt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val store: String? = null,
    val total: Double? = null,
    val tax: Double? = null,
    val scannedAt: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)
