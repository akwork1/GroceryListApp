package com.atticus.grocerylistapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atticus.grocerylistapp.data.dao.*
import com.atticus.grocerylistapp.data.entity.*

@Database(
    entities = [
        PantryItem::class,
        PlannedLine::class,
        Purchase::class,
        Receipt::class,
        IntakeEstimate::class,
        SessionEvent::class            // ✅ new
    ],
    version = 2,                      // ✅ bump db version
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun pantry(): PantryDao
    abstract fun planned(): PlannedDao
    abstract fun purchase(): PurchaseDao
    abstract fun sessionEvent(): SessionEventDao   // ✅ new
}
