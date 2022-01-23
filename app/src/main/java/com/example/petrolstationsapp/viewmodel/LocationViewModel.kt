package com.example.petrolstationsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petrolstationsapp.model.Location

class LocationViewModel : ViewModel() {
    val location: MutableLiveData<Location> = MutableLiveData()
}