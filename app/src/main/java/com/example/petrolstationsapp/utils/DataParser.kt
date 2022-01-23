package com.example.petrolstationsapp.utils

import com.example.petrolstationsapp.model.Location
import com.example.petrolstationsapp.model.Station
import org.json.JSONObject

class DataParser {
    companion object {
        fun parseStationsResponse(it: String): List<Station> {
            val resultsJSON = JSONObject(it).getJSONArray("results")
            val placesList = ArrayList<Station>()
            for (i in 0 until resultsJSON.length()) {
                val ob: JSONObject = resultsJSON.getJSONObject(i)
                val name = if (ob.has("name")) ob.getString("name") else "Brak nazwy"
                val locationJSON = ob.getJSONObject("geometry").getJSONObject("location")
                val location = Location(locationJSON.getDouble("lat"), locationJSON.getDouble("lng"))
                val address = if (ob.has("vicinity")) ob.getString("vicinity") else "Brak adresu"
                val rating = if (ob.has("rating") && ob.getDouble("rating") != 0.0) ob.getDouble("rating") else 0.0
                val ratingsCount = if(ob.has("user_ratings_total")) ob.getInt("user_ratings_total") else 0
                var isOpenNow: Boolean? = null
                if (ob.has("opening_hours")) {
                    if (ob.getJSONObject("opening_hours").has("open_now"))
                        isOpenNow = ob.getJSONObject("opening_hours").getBoolean("open_now")
                }
                placesList.add(Station(name, location, address, rating.toFloat(), isOpenNow,ratingsCount))
            }
            return placesList
        }
    }
}