package com.example.petrolstationsapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.petrolstationsapp.model.Location

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAll():List<Location>?

    @Insert
    fun insert(location: Location)

    @Delete
    fun delete(location: Location)
}