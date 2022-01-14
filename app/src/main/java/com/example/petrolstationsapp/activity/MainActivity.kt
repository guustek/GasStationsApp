package com.example.petrolstationsapp.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.adapter.PetrolStationAdapter
import com.example.petrolstationsapp.databinding.ActivityMainBinding
import com.example.petrolstationsapp.model.PetrolStation
import com.example.petrolstationsapp.utils.DataParser


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private var placesList: List<PetrolStation> = ArrayList()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        recyclerView = binding.stationsListView
        recyclerView.apply {
            adapter = PetrolStationAdapter(placesList)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        binding.button2.setOnClickListener {
            val bestProvider: String = locationManager.getBestProvider(Criteria(), true).toString()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    997
                )
            } else {
                val location: Location? = locationManager.getLastKnownLocation(bestProvider)
                if (location != null) {
                    getPlaces(this,location, 2000.0,"gas_station")
                }
            }
        }
    }

    private fun getPlaces(context: Context,location: Location,searchRadius:Double,placeType:String) {
        val queue: RequestQueue = Volley.newRequestQueue(context)

        val baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        val builder: StringBuilder = StringBuilder()
        builder.append(baseUrl)
        builder.append("location=${location.latitude},${location.longitude}")
        builder.append("&radius=$searchRadius")
        builder.append("&type=$placeType")
        builder.append("&key=${getString(R.string.maps_key)}")
        val requestUrl: String = builder.toString()
        val request = StringRequest(Request.Method.GET, requestUrl,
            {
                if (it != null) {
                    placesList = DataParser.parseNearbyPlacesResponse(it)
                    this.recyclerView.adapter = PetrolStationAdapter(placesList)
                }
            },
            {
                println("Error przy get")
            })
        queue.add(request)
    }

}
