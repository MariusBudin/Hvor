package com.mariusbudin.hvor.data.common

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mariusbudin.hvor.data.venues.VenuesDao
import com.mariusbudin.hvor.data.venues.model.VenueEntity

/**
 * Not real as we're not using a Database for this version, it's meant as an example
 * of its placement in this particular architecture
 */
@Database(
    entities = [VenueEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun venuesDao(): VenuesDao

    companion object {

        private const val DB_NAME = "venues_db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }

}