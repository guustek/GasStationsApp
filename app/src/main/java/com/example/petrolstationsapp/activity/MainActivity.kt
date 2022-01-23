package com.example.petrolstationsapp.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
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


class MainActivity : AppCompatActivity() {

    companion object {
        var permissionDialogShown: Boolean = false
    }

    private val radius = 5000

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var stationsModel: StationViewModel
    private lateinit var locationModel: LocationViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        stationsModel = ViewModelProvider(this)[StationViewModel::class.java]
        locationModel = ViewModelProvider(this)[LocationViewModel::class.java]

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        val placesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                locationModel.location.value = Location(it.latitude, it.longitude)
                stationsModel.stations.value = ArrayList()
            } else {
                Snackbar.make(
                    binding.root,
                    "Nie udało się pobrać aktualnej lokalizacji!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        binding.locationButton.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    locationModel.location.value = Location(it.latitude, it.longitude)
                } else {
                    Snackbar.make(
                        binding.root,
                        "Nie udało się pobrać aktualnej lokalizacji!",
                        Snackbar.LENGTH_LONG
                    ).show()
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
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(
            object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    locationModel.location.value =
                        Location(place.latLng.latitude, place.latLng.longitude)
                }

                override fun onError(status: Status) {
                    Snackbar.make(binding.root, "Nie wybrano lokalizacji!", Snackbar.LENGTH_LONG).show()
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
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
        return navController.navigateUp(appBarConfiguration)
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
               Snackbar.make(binding.root,"Brak połączenia internetowego!",Snackbar.LENGTH_INDEFINITE).show()
            })
        queue.add(request)
    }
}
