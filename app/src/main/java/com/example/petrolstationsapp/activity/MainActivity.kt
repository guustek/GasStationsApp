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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.database.MyDatabase
import com.example.petrolstationsapp.databinding.ActivityMainBinding
import com.example.petrolstationsapp.model.Location
import com.example.petrolstationsapp.utils.DataParser
import com.example.petrolstationsapp.utils.LocationService
import com.example.petrolstationsapp.viewmodel.LocationViewModel
import com.example.petrolstationsapp.viewmodel.SavedLocationsViewModel
import com.example.petrolstationsapp.viewmodel.SavedStationsViewModel
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
        const val LOCATION_REQUEST: Int = 111
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var networkLocationListener: LocationListener
    private lateinit var gpsLocationListener: LocationListener

    private lateinit var stationsModel: StationViewModel
    private lateinit var locationModel: LocationViewModel

    private lateinit var savedStationsModel: SavedStationsViewModel
    private lateinit var savedLocationsModel: SavedLocationsViewModel

    private lateinit var database: MyDatabase

    private lateinit var binding: ActivityMainBinding


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        stationsModel = ViewModelProviders.of(this)[StationViewModel::class.java]
        locationModel = ViewModelProviders.of(this)[LocationViewModel::class.java]

        savedStationsModel = ViewModelProviders.of(this)[SavedStationsViewModel::class.java]
        savedLocationsModel = ViewModelProviders.of(this)[SavedLocationsViewModel::class.java]

        database = MyDatabase.getDatabase(this)

        savedStationsModel.stations.value = database.stationDao().getAll()
        savedLocationsModel.locations.value = database.locationDao().getAll()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gpsLocationListener = LocationListener {
            if (locationModel.location.value == null) {
                locationModel.location.value = Location(it.latitude, it.longitude, getString(R.string.no_address))
                locationManager.removeUpdates(gpsLocationListener)
            } else
                locationManager.removeUpdates(gpsLocationListener)
        }
        networkLocationListener = LocationListener {
            if (locationModel.location.value == null) {
                locationModel.location.value = Location(it.latitude, it.longitude, getString(R.string.no_address))
                locationManager.removeUpdates(networkLocationListener)
            } else
                locationManager.removeUpdates(networkLocationListener)
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, gpsLocationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1f, networkLocationListener)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.locationButton.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    locationModel.location.value =
                        Location(it.latitude, it.longitude, getString(R.string.no_address))
                } else
                    buildNoGpsAlert()
            }
        }

        binding.searchButton.setOnClickListener {
            val location = locationModel.location.value
            if (location != null)
                getPlaces(
                    this, location.latitude, location.longitude,
                    preferences.getString("searchRadius", "5000.0")?.toDouble()!!
                )
            else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_location),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.saveLocationButton.setOnClickListener {
            val location = locationModel.location.value
            if (location != null) {
                if (location.address == getString(R.string.no_address))
                    location.address = LocationService.getAddress(this, location.latitude, location.longitude)
                savedLocationsModel.locations.value = database.locationDao().getAll()
                if (savedLocationsModel.locations.value?.contains(location) == false) {
                    database.locationDao().insert(location)
                    savedLocationsModel.locations.value = database.locationDao().getAll()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.saved_location),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.location_exists),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_location),
                    Snackbar.LENGTH_LONG
                ).show()
            }
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
                        Location(place.latLng.latitude, place.latLng.longitude, place.address)
                    println("nie error")
                }

                override fun onError(status: Status) {
                    println("error")
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                false
            }
            R.id.saved_places -> {
                val intent = Intent(this, SavedPlacesActivity::class.java)
                startActivityForResult(intent, LOCATION_REQUEST)
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        if (locationModel.location.value == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, gpsLocationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, networkLocationListener)
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(gpsLocationListener)
        locationManager.removeUpdates(networkLocationListener)
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
                    stationsModel.stations.value = DataParser.parseStationsResponse(it, this)
                }
            },
            {
                Snackbar.make(binding.root, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE).show()
            })
        queue.add(request)
    }

    private fun buildNoGpsAlert() {
        this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.location_off))
                setCancelable(true)
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                setNegativeButton(
                    getString(R.string.close)
                ) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == LOCATION_REQUEST) {
            if (data?.hasExtra("latitude") == true && data.hasExtra("longitude") && data.hasExtra("address")) {
                locationModel.location.value = Location(
                    data.getDoubleExtra("latitude", 0.0), data.getDoubleExtra("longitude", 0.0),
                    data.getStringExtra("address")!!
                )
            }
        }
    }
}
