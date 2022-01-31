package com.example.petrolstationsapp.fragment


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.appolica.interactiveinfowindow.InfoWindow
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.FragmentMapsBinding
import com.example.petrolstationsapp.model.Location
import com.example.petrolstationsapp.model.Station
import com.example.petrolstationsapp.viewmodel.LocationViewModel
import com.example.petrolstationsapp.viewmodel.StationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*


class MapsFragment : Fragment() {

    private var openedInfoWindow: InfoWindow? = null
    private var mapFragment: MapInfoWindowFragment? = null
    private var _binding: FragmentMapsBinding? = null
    private lateinit var map: GoogleMap
    private lateinit var stationsModel: StationViewModel
    private lateinit var locationModel: LocationViewModel
    private var cameraZoomLevel: Float = 11f
    private lateinit var preferences: SharedPreferences
    private var firstLoad: Boolean = true
    private var networkChangeReceiver: BroadcastReceiver? = null

    private val binding get() = _binding!!


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        if (preferences.getBoolean("darkMode", false)) {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    activity!!, R.raw.map_night_style
                )
            )
        }
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        //map.setInfoWindowAdapter(MarkerInfoWindowAdapter(context!!))
        map.setOnMarkerClickListener {
            if (!it.isDraggable) {
                openedInfoWindow = InfoWindow(
                    it,
                    InfoWindow.MarkerSpecification(0, 100),
                    InfoWindowFragment(it, preferences.getBoolean("darkMode", false))
                )
                mapFragment?.infoWindowManager()?.toggle(openedInfoWindow!!, true)
            }
            true
        }
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {
                map.animateCamera(CameraUpdateFactory.newLatLng(p0.position))
            }

            override fun onMarkerDragEnd(p0: Marker) {
                locationModel.location.value = Location(p0.position.latitude, p0.position.longitude, getString(R.string.no_address))
            }

            override fun onMarkerDragStart(p0: Marker) {
            }

        })
        map.setOnCameraIdleListener {
            cameraZoomLevel = map.cameraPosition.zoom
        }
        if (firstLoad) {
            if (locationModel.location.value != null) {
                firstLoad = false
                map.clear()
                map.moveCamera(CameraUpdateFactory.zoomTo(cameraZoomLevel))
                map.moveCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(
                            locationModel.location.value!!.latitude,
                            locationModel.location.value!!.longitude
                        )
                    )
                )
                updateMarkers()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        preferences = PreferenceManager.getDefaultSharedPreferences(activity!!)
        preferences.registerOnSharedPreferenceChangeListener { _: SharedPreferences, key: String ->
            if (key == "showCircle")
                updateMarkers()
        }
//        if(networkChangeReceiver==null){
//            networkChangeReceiver = object : BroadcastReceiver() {
//                override fun onReceive(p0: Context?, p1: Intent?) {
//                   // mapFragment?.getMapAsync(callback)
//                }
//            }
//        }
        binding.switchingButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.map_to_list)
        }

        locationModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        locationModel.location.observe(viewLifecycleOwner) {
            if (this::map.isInitialized) {
                firstLoad = false
                map.clear()
                if (openedInfoWindow != null)
                    mapFragment?.infoWindowManager()?.hide(openedInfoWindow!!)
                map.animateCamera(CameraUpdateFactory.zoomTo(cameraZoomLevel))
                map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                updateMarkers()
            }
        }
        stationsModel = ViewModelProvider(requireActivity())[StationViewModel::class.java]
        stationsModel.stations.observe(viewLifecycleOwner) {
            if (this::map.isInitialized) {
                map.clear()
                updateMarkers()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapInfoWindowFragment?

        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // activity?.registerReceiver(networkChangeReceiver,IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        if (this::map.isInitialized) {
            map.clear()
            updateMarkers()
        }
    }

    private fun updateMarkers() {
        if (stationsModel.stations.value != null) {
            for (station in stationsModel.stations.value!!)
                map.addMarker(buildMarker(station))
        }
        if (locationModel.location.value != null) {
            if (preferences.getBoolean("showCircle", true)) {
                map.addCircle(
                    CircleOptions().center(
                        LatLng(
                            locationModel.location.value!!.latitude,
                            locationModel.location.value!!.longitude
                        )
                    ).radius((preferences.getString("searchRadius", "5000.0")?.toDouble() ?: 5000.0))
                        .strokeColor(Color.BLUE).strokeWidth(3f).fillColor(0x220000FF)
                )
            }
            map.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            locationModel.location.value!!.latitude,
                            locationModel.location.value!!.longitude
                        )
                    )
                    .icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.pin)
                    )
                    .draggable(true)
            )
        }
    }

    private fun buildMarker(station: Station): MarkerOptions {
        val options = MarkerOptions()
        options.position(LatLng(station.location.latitude, station.location.longitude))
        options.title(station.name)
        options.snippet("${station.rating};${station.location.address};${station.isOpenNow};${station.ratingsCount}")
        return options
    }
}
