package com.example.petrolstationsapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Station(
    val name: String?,
    @Embedded val location: Location,
    val rating: Float?,
    val isOpenNow: Boolean?,
    val ratingsCount: Int?,
    @PrimaryKey(autoGenerate = true) val stationId: Int = 0
){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Station

        if (name != other.name) return false
        if (location != other.location) return false
        if (rating != other.rating) return false
        if (isOpenNow != other.isOpenNow) return false
        if (ratingsCount != other.ratingsCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + location.hashCode()
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (isOpenNow?.hashCode() ?: 0)
        result = 31 * result + (ratingsCount ?: 0)
        return result
    }
}


