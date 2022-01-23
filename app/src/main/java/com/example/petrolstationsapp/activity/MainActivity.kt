package com.example.petrolstationsapp.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.ActivityMainBinding
import com.example.petrolstationsapp.model.Location
import com.example.petrolstationsapp.utils.DataParser
import com.example.petrolstationsapp.viewmodel.LocationViewModel
import com.example.petrolstationsapp.viewmodel.StationViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar


class MainActivity : DarkLightModeActivity() {

    companion object {
        var permissionDialogShown: Boolean = false
    }

    private val AUTOCOMPLETE_RESULT_CODE: Int = 2115
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var networkLocationListener: LocationListener
    private lateinit var gpsLocationListener: LocationListener

    private lateinit var stationsModel: StationViewModel
    private lateinit var locationModel: LocationViewModel
    private lateinit var binding: ActivityMainBinding


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)


        stationsModel = ViewModelProvider(this)[StationViewModel::class.java]
        locationModel = ViewModelProvider(this)[LocationViewModel::class.java]

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        val placesClient = Places.createClient(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gpsLocationListener = LocationListener {
            if (locationModel.location.value == null) {
                locationModel.location.value = Location(it.latitude, it.longitude)
                locationManager.removeUpdates(gpsLocationListener)
            } else
                locationManager.removeUpdates(gpsLocationListener)
        }
        networkLocationListener = LocationListener {
            if (locationModel.location.value == null) {
                locationModel.location.value = Location(it.latitude, it.longitude)
                locationManager.removeUpdates(networkLocationListener)
            } else
                locationManager.removeUpdates(networkLocationListener)
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, gpsLocationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, networkLocationListener)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.locationButton.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    locationModel.location.value = Location(it.latitude, it.longitude)
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.apply {
                        setMessage("Lokalizacja wyłączona, czy chcesz ją włączyć?")
                        setCancelable(true)
                        setPositiveButton("Tak") { _, _ ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }

                        setNegativeButton(
                            "Zamknij"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                    }
                    builder.create()
                    builder.show()
                }
            }
        }

        binding.searchButton.setOnClickListener {
            val location = locationModel.location.value
            if (location != null)
                getPlaces(
                    this, location.latitude, location.longitude,
                    preferences.getString("searchRadius", "5000.0")?.toDouble() ?: 5000.0
                )
            else {
                Snackbar.make(
                    binding.root,
                    "Nie wybrano lokalizacji!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
//            else{
//                Snackbar.make(
//                    binding.root,
//                    "Brak połączenia internetowego!",
//                    Snackbar.LENGTH_LONG
//                ).show()
//            }
        }

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setTypeFilter(TypeFilter.ADDRESS)
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(
            object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    locationModel.location.value =
                        Location(place.latLng.latitude, place.latLng.longitude)
                }

                override fun onError(status: Status) {
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        if (preferences.getBoolean("darkMode", false))
            menu.getItem(0).icon = getDrawable(R.drawable.search_white)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }


    private fun getPlaces(context: Context, latitude: Double, longitude: Double, radius: Double) {
        val queue: RequestQueue = Volley.newRequestQueue(context)

        val baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        val builder: StringBuilder = StringBuilder()
        builder.append(baseUrl)
        builder.append("location=$latitude,$longitude")
        builder.append("&radius=$radius")
        builder.append("&keyword=gas station")
        builder.append("&rankby=prominence")
        builder.append("&language=pl")
        builder.append("&key=${getString(R.string.google_maps_key)}")
        val requestUrl: String = builder.toString()
        val request = StringRequest(Request.Method.GET, requestUrl,
            {
                if (it != null) {
                    stationsModel.stations.value = DataParser.parseStationsResponse(it)
                }
            },
            {
                Snackbar.make(binding.root, "Brak połączenia internetowego!", Snackbar.LENGTH_INDEFINITE).show()
            })
        queue.add(request)
    }
}
