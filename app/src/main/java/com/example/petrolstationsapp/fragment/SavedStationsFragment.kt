package com.example.petrolstationsapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petrolstationsapp.adapter.SavedStationAdapter
import com.example.petrolstationsapp.databinding.FragmentStationsListBinding
import com.example.petrolstationsapp.viewmodel.SavedStationsViewModel

class SavedStationsFragment : Fragment() {

    private var _binding: FragmentStationsListBinding? = null

    private val binding get() = _binding!!

    private lateinit var savedStationsModel: SavedStationsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStationsListBinding.inflate(inflater, container, false)

        savedStationsModel = ViewModelProvider(requireActivity())[SavedStationsViewModel::class.java]
        savedStationsModel.stations.observe(viewLifecycleOwner) {
            val adapter = binding.stationsListView.adapter as SavedStationAdapter
            adapter.notifyChange(it!!)
        }


        binding.stationsListView.apply {
            adapter = SavedStationAdapter(context,savedStationsModel)
            layoutManager = LinearLayoutManager(context)
        }
        binding.switchingButton.isVisible = false
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}