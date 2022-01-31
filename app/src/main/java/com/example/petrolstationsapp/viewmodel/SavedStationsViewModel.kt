package com.example.petrolstationsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petrolstationsapp.model.Station

class SavedStationsViewModel :  ViewModel()  {
    val stations: MutableLiveData<List<Station>> = MutableLiveData()
}