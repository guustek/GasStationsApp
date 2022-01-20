package com.example.petrolstationsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchDataViewModel : ViewModel() {
    val searchData: MutableLiveData<SearchData> = MutableLiveData()
}