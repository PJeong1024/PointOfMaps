package com.example.pointofmaps.classes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserImg::class], version = 1)
abstract class ImgMapDatabase : RoomDatabase() {
    abstract fun userDao(): UserImgDao

    companion object {
        @Volatile
        private var INSTANCE: ImgMapDatabase? = null

        fun getDatabase(context: Context): ImgMapDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): ImgMapDatabase {
            return Room.databaseBuilder(context.applicationContext, ImgMapDatabase::class.java, "img_database").build()
        }
    }
}