package com.example.petrolstationsapp.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.ActivityMainBinding
import com.example.petrolstationsapp.fragment.LoadingFragment
import com.example.petrolstationsapp.fragment.MapsFragment
import com.example.petrolstationsapp.fragment.StationsListFragment
import com.example.petrolstationsapp.model.Coordinate
import com.example.petrolstationsapp.model.PetrolStation
import com.example.petrolstationsapp.utils.DataParser


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var stationList: List<PetrolStation> = ArrayList()
    private var location: Location? = null
    private var isLocationPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
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
            isLocationPermissionGranted = true
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val bestProvider: String = locationManager.getBestProvider(Criteria(), true).toString()
            location = locationManager.getLastKnownLocation(bestProvider)
            if (location != null) {
                val location = location!!
                binding.textView2.text =
                    DataParser.getAddressFromCoordinates(
                        Coordinate(location.latitude, location.longitude),
                        this
                    )
                getPlaces(this, location, 10000.0, "gas_station")
                supportFragmentManager.beginTransaction().apply {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.fragmentContainer.id, LoadingFragment())
                        commit()
                    }
                }
            }

            binding.switchingButton.setOnClickListener {
                val currentFragment = supportFragmentManager.fragments.last()
                if (currentFragment is MapsFragment) {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.fragmentContainer.id, StationsListFragment(stationList))
                        commit()
                    }
                } else {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.fragmentContainer.id, MapsFragment(location!!,stationList))
                        commit()
                    }
                }
            }
        }
    }

    private fun getPlaces(context: Context, location: Location, searchRadius: Double, placeType: String) {
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
                    stationList = DataParser.parseNearbyPlacesResponse(it)
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.fragmentContainer.id, MapsFragment(location,stationList))
                        commit()
                    }
                }
            },
            {
                println("Error przy get")
            })
        queue.add(request)
    }
}
