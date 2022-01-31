package com.example.petrolstationsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petrolstationsapp.model.Location

class SavedLocationsViewModel:ViewModel() {
    val locations: MutableLiveData<List<Location>> = MutableLiveData()

}