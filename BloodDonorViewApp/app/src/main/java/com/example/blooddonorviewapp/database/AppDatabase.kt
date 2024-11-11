package com.example.blooddonorviewapp.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Donor::class, DonationEvent::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val donorDao: DonorDao
    abstract val donationEventDao: DonationEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "donor_database"
                    )
                        .fallbackToDestructiveMigration()  // Use destructive migration to clear data if needed
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

