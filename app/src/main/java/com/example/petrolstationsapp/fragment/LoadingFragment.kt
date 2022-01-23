package com.example.petrolstationsapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.petrolstationsapp.databinding.FragmentLoadingBinding
import com.example.petrolstationsapp.viewmodel.LocationViewModel
import com.example.petrolstationsapp.viewmodel.StationViewModel

class LoadingFragment : Fragment() {

    private var _binding: FragmentLoadingBinding? = null
    private lateinit var stationsModel: StationViewModel
    private lateinit var searchDataModel: LocationViewModel

    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)

        searchDataModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        stationsModel = ViewModelProvider(requireActivity())[StationViewModel::class.java]
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}