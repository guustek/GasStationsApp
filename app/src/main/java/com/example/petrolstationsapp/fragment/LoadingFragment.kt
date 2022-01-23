package com.example.petrolstationsapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.FragmentLoadingBinding
import com.example.petrolstationsapp.viewmodel.LocationViewModel

class LoadingFragment : Fragment() {

    private var _binding: FragmentLoadingBinding? = null
    private lateinit var locationModel: LocationViewModel

    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)

        locationModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        locationModel.location.observe(viewLifecycleOwner) {
            Navigation.findNavController(binding.root).navigate(R.id.loading_to_map)
        }

        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            buildNoGpsAlert()

        return binding.root
    }

    private fun buildNoGpsAlert() {
        if (locationModel.location.value == null) {
            activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setMessage("Lokalizacja wyłączona, czy chcesz ją włączyć?")
                    setCancelable(true)
                    setPositiveButton("Tak") { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                    setNeutralButton(
                        "Przejdź do mapy"
                    ) { _, _ ->
                        Navigation.findNavController(binding.root).navigate(R.id.loading_to_map)
                    }
                    setNegativeButton(
                        "Zamknij"
                    ) { dialog, _ ->
                        dialog.cancel()
                    }
                }
                builder.create()
                builder.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}