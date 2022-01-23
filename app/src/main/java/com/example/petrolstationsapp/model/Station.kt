package com.example.petrolstationsapp.model

data class Station(
    val name: String?,
    val location: Location,
    val address: String?,
    val rating: Float?,
    val isOpenNow: Boolean?,
    val ratingsCount: Int?
)
