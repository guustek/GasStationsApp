package com.example.petrolstationsapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.example.petrolstationsapp.model.Coordinate
import com.example.petrolstationsapp.model.PetrolStation
import org.json.JSONObject
import java.io.IOException
import java.util.*

class DataParser {
    companion object {
        fun parseNearbyPlacesResponse(it: String): List<PetrolStation> {
            val JSONResults = JSONObject(it).getJSONArray("results")
            val placesList = ArrayList<PetrolStation>()
            for (i in 0 until JSONResults.length()) {
                val ob: JSONObject = JSONResults.getJSONObject(i)
                val name = ob.getString("name");
                val JSONLocation = ob.getJSONObject("geometry").getJSONObject("location")
                val location = Coordinate(JSONLocation.getDouble("lat"), JSONLocation.getDouble("lng"))
                placesList.add(PetrolStation(name, location))
            }
            return placesList
        }

        private fun parseLocation(location: Location,context: Context) {
            val addresses: List<Address>?
            val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
            val latitude = location.latitude
            val longitude = location.longitude
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address: String = addresses[0].getAddressLine(0)
                    val city: String = addresses[0].locality
                    val state: String = addresses[0].adminArea
                    val country: String = addresses[0].countryName
                    val postalCode: String = addresses[0].postalCode
                    val knownName: String = addresses[0].featureName
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}