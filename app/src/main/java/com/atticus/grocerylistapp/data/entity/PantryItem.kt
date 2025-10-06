package com.atticus.grocerylistapp.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["normalizedName"], unique = true)])
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,
    val normalizedName: String,
    val brand: String? = null,
    val sizeGrams: Int? = null,
    val defaultCaloriesPer100g: Int? = null,
    val defaultProteinPer100g: Int? = null,
    val defaultCarbsPer100g: Int? = null,
    val defaultFatPer100g: Int? = null,
    val isArchived: Boolean = false,
    val liked: Boolean? = null,
    val disliked: Boolean? = null
)
