package com.example.myairportapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AirportEntity::class, FavoriteRouteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlightSearchDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun favoriteRouteDao(): FavoriteRouteDao

    companion object {
        @Volatile
        private var INSTANCE: FlightSearchDatabase? = null

        fun getDatabase(context: Context): FlightSearchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightSearchDatabase::class.java,
                    "flight_search.db"
                )
                    .createFromAsset("flight_search.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}