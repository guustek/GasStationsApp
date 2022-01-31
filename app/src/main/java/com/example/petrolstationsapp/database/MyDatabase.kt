package com.example.petrolstationsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petrolstationsapp.model.Location
import com.example.petrolstationsapp.model.Station

@Database(entities = [Location::class, Station::class], version = 1)
abstract class MyDatabase : RoomDatabase() {

    companion object {
        private var instance: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(context, MyDatabase::class.java, "database")
                    .allowMainThreadQueries().build()
            return instance!!
        }
    }

    abstract fun stationDao(): StationDao

    abstract fun locationDao(): LocationDao
}