package com.atticus.grocerylistapp.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.atticus.grocerylistapp.data.db.AppDb
import com.atticus.grocerylistapp.repo.GroceryRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDb =
        Room.databaseBuilder(ctx, AppDb::class.java, "grocery.db").fallbackToDestructiveMigration().build()

    @Provides @Singleton
    fun provideRepo(db: AppDb, @ApplicationContext ctx: Context): GroceryRepo =
        GroceryRepo(db, ctx)
}
