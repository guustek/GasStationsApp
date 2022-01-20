package com.example.petrolstationsapp.utils

import com.example.petrolstationsapp.model.Location
import com.example.petrolstationsapp.model.Station
import org.json.JSONObject

class DataParser {
    companion object {
        fun parseResponse(it: String): List<Station> {
            val resultsJSON = JSONObject(it).getJSONArray("results")
            val placesList = ArrayList<Station>()
            for (i in 0 until resultsJSON.length()) {
                val ob: JSONObject = resultsJSON.getJSONObject(i)
                val name = if (ob.has("name")) ob.getString("name") else null
                val locationJSON = ob.getJSONObject("geometry").getJSONObject("location")
                val location = Location(locationJSON.getDouble("lat"), locationJSON.getDouble("lng"))
                val address = if (ob.has("vicinity")) ob.getString("vicinity") else null
                val rating = if (ob.has("rating")) ob.getDouble("rating") else null
                val isOpenNow =
                    if (ob.has("opening_hours")) ob.getJSONObject("opening_hours").getBoolean("open_now") else null
                placesList.add(Station(name, location, address, rating, isOpenNow))
            }
            return placesList
        }
    }
}