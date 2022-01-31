package com.example.petrolstationsapp.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.database.MyDatabase
import com.example.petrolstationsapp.databinding.ActivitySavedPlacesBinding
import com.example.petrolstationsapp.viewmodel.SavedLocationsViewModel
import com.example.petrolstationsapp.viewmodel.SavedStationsViewModel

class SavedPlacesActivity : DarkLightModeActivity() {

    private lateinit var binding: ActivitySavedPlacesBinding
    private lateinit var savedStationsModel: SavedStationsViewModel
    private lateinit var savedLocationsModel: SavedLocationsViewModel
    private lateinit var database: MyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedPlacesBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title=getString(R.string.saved_places)
        setContentView(binding.root)
        savedStationsModel = ViewModelProviders.of(this)[SavedStationsViewModel::class.java]
        savedLocationsModel = ViewModelProviders.of(this)[SavedLocationsViewModel::class.java]

        database = MyDatabase.getDatabase(this)

        savedStationsModel.stations.value = database.stationDao().getAll()
        savedLocationsModel.locations.value = database.locationDao().getAll()

        binding.savedStationsButton.setOnClickListener{
            Navigation.findNavController(binding.fragmentContainerView).navigate(R.id.saved_locations_to_saved_stations)
        }
        binding.savedPlacesButton.setOnClickListener{
            Navigation.findNavController(binding.fragmentContainerView).navigate(R.id.saved_stations_to_saved_locations)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
