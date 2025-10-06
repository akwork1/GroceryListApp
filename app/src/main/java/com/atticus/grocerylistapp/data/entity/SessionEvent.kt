package com.atticus.grocerylistapp.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Logs one row whenever the user marks an item as bought or out of stock.
 * type: "bought" | "oos"
 * priceTotal is optional (we also persist a Purchase row for bought).
 */
@Entity(indices = [Index("itemId"), Index("happenedAt"), Index("type")])
data class SessionEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val type: String,            // "bought" | "oos"
    val happenedAt: Long,        // System.currentTimeMillis() at action time
    val priceTotal: Double? = null
)