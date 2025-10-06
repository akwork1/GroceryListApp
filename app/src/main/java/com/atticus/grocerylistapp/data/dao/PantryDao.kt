package com.atticus.grocerylistapp.data.dao

import androidx.room.*
import com.atticus.grocerylistapp.data.entity.PantryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) fun insert(item: PantryItem): Long
    @Update fun update(item: PantryItem)
    @Query("SELECT * FROM PantryItem WHERE isArchived=0 ORDER BY displayName")
    fun allActive(): Flow<List<PantryItem>>

    @Query("SELECT * FROM PantryItem WHERE normalizedName LIKE :q || '%' ORDER BY displayName LIMIT 50")
    fun search(q: String): List<PantryItem>

    @Query("SELECT * FROM PantryItem WHERE id=:id") fun byId(id: Long): PantryItem?
    @Query("SELECT * FROM PantryItem WHERE normalizedName=:norm LIMIT 1") fun byNorm(norm: String): PantryItem?
}
