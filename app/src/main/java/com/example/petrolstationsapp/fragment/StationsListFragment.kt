package com.example.petrolstationsapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petrolstationsapp.adapter.PetrolStationAdapter
import com.example.petrolstationsapp.databinding.FragmentStationsListBinding
import com.example.petrolstationsapp.model.PetrolStation


class StationsListFragment(private val stationList: List<PetrolStation>) : Fragment() {

    private var _binding:FragmentStationsListBinding?=null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentStationsListBinding.inflate(inflater, container, false)
        binding.stationsListView.apply {
            adapter= PetrolStationAdapter(stationList)
            layoutManager = LinearLayoutManager(context)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}