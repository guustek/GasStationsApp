package com.example.petrolstationsapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.petrolstationsapp.model.Station

@Dao
interface StationDao {

    @Query("SELECT * FROM station")
    fun getAll():List<Station>?

    @Insert
    fun insert(station: Station)

    @Delete
    fun delete(station: Station)
}