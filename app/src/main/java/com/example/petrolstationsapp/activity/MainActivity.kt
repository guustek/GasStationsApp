package com.example.petrolstationsapp.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.ActivityMainBinding
import com.example.petrolstationsapp.viewmodel.SearchData
import com.example.petrolstationsapp.viewmodel.SearchDataViewModel
import com.example.petrolstationsapp.viewmodel.StationViewModel
import com.example.petrolstationsapp.utils.DataParser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    companion object {
        const val LOCATION_PERMISSIONS_REQUEST_CODE = 997
    }

    private val radius = 5000

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var stationsModel: StationViewModel
    private lateinit var searchDataModel: SearchDataViewModel

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        stationsModel = ViewModelProvider(this)[StationViewModel::class.java]
        searchDataModel = ViewModelProvider(this)[SearchDataViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                searchDataModel.searchData.value = SearchData(it.latitude, it.longitude, radius)
                stationsModel.stations.value = ArrayList()
            }
        }



        binding.button.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
//                    binding.textView2.text =
//                        DataParser.getAddressFromCoordinates(Coordinate(it.latitude, it.longitude), this)
                    searchDataModel.searchData.value = SearchData(it.latitude, it.longitude, radius)
                    getPlaces(this, it.latitude, it.longitude, radius, "gas station")
                } else {
                    Snackbar.make(
                        binding.root,
                        "Nie udało się pobrać aktualnej lokalizacji!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }
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
            R.id.settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun getPlaces(context: Context, latitude: Double, longitude: Double, radius: Int, placeType: String) {
        val queue: RequestQueue = Volley.newRequestQueue(context)

        val baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        val builder: StringBuilder = StringBuilder()
        builder.append(baseUrl)
        builder.append("location=$latitude,$longitude")
        builder.append("&radius=$radius")
        builder.append("&keyword=$placeType")
        builder.append("&key=${getString(R.string.google_maps_key)}")
        val requestUrl: String = builder.toString()
        val request = StringRequest(Request.Method.GET, requestUrl,
            {
                if (it != null) {
                    stationsModel.stations.value = DataParser.parseResponse(it)
                }
            },
            {
                println("Error przy get")
            })
        queue.add(request)
    }

    private fun showTurnOnGpsDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setMessage("GPS nie jest włączony. Czy chcesz udać się do ustawień?")
        alertDialog.setPositiveButton("Ustawienia") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        alertDialog.setNegativeButton("Anuluj") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }
}
