package com.example.petrolstationsapp.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.FragmentMapsBinding
import com.example.petrolstationsapp.model.Station
import com.example.petrolstationsapp.viewmodel.SearchDataViewModel
import com.example.petrolstationsapp.viewmodel.StationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private lateinit var map: GoogleMap
    private lateinit var stationsModel: StationViewModel
    private lateinit var searchDataModel: SearchDataViewModel

    private val binding get() = _binding!!


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        googleMap.isMyLocationEnabled = true
        searchDataModel = ViewModelProvider(requireActivity())[SearchDataViewModel::class.java]
        searchDataModel.searchData.observe(viewLifecycleOwner) {
            map.moveCamera(CameraUpdateFactory.zoomTo(11f))
            map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
//            map.addCircle(
//                CircleOptions().center(LatLng(it.latitude, it.longitude)).radius(it.radius.toDouble())
//                    .strokeColor(Color.BLUE).strokeWidth(3f).fillColor(0x220000FF)
//            )
        }

        stationsModel = ViewModelProvider(requireActivity())[StationViewModel::class.java]
        stationsModel.stations.observe(viewLifecycleOwner) {
            updateMarkers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        binding.switchingButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.map_to_list)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateMarkers() {
        map.clear()
        for (station in stationsModel.stations.value!!)
            map.addMarker(buildMarker(station))
        map.addCircle(
            CircleOptions().center(
                LatLng(
                    searchDataModel.searchData.value!!.latitude,
                    searchDataModel.searchData.value!!.longitude
                )
            ).radius(searchDataModel.searchData.value!!.radius.toDouble())
                .strokeColor(Color.BLUE).strokeWidth(3f).fillColor(0x220000FF)
        )
    }

    private fun buildMarker(station: Station): MarkerOptions {
        val options = MarkerOptions()
        options.position(LatLng(station.location.latitude, station.location.longitude))
        options.title(station.name)
        options.snippet(station.address)
        return options
    }

    private fun showTurnOnGpsDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog.setMessage("GPS nie jest włączony. Czy chcesz udać się do ustawień?")
        alertDialog.setPositiveButton("Ustawienia") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        alertDialog.setNegativeButton("Anuluj") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }

}