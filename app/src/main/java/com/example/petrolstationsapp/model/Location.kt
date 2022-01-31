package com.example.petrolstationsapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    val latitude: Double,
    val longitude: Double,
    var address: String,
    @PrimaryKey(autoGenerate = true) val locationId: Int = 0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}
