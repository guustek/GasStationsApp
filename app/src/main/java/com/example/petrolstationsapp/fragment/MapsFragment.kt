package com.example.petrolstationsapp.fragment

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.FragmentMapsBinding
import com.example.petrolstationsapp.model.PetrolStation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment(location: Location, stationList: List<PetrolStation>) : Fragment() {

    private var binding: FragmentMapsBinding? = null
    private lateinit var currentLocationMarker: Marker
    private lateinit var map: GoogleMap


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val latLng = LatLng(location.latitude, location.longitude)
        currentLocationMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Twoja lokalizacja"))!!
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        for (station: PetrolStation in stationList) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(station.location.latitude, station.location.longitude))
                    .title(station.name)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        binding!!.localizeButton.setOnClickListener {
            map.animateCamera(CameraUpdateFactory.newLatLng(currentLocationMarker.position))
        }
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}