package com.example.mobdev_project

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Create the database
@Database(entities = [Tracking::class], version = 1)
abstract class TrackingDatabase : RoomDatabase() {

    abstract fun getDao(): TrackingDAO

    companion object {
        @Volatile
        private var INSTANCE: TrackingDatabase? = null
        fun getInstance(context: Context): TrackingDatabase {

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrackingDatabase::class.java, "tracking_db_v2"
                )
                    .build()
            }

        }
    }

}