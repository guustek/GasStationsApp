package com.example.petrolstationsapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petrolstationsapp.adapter.SavedLocationsAdapter
import com.example.petrolstationsapp.databinding.FragmentStationsListBinding
import com.example.petrolstationsapp.viewmodel.SavedLocationsViewModel

class SavedLocationsFragment : Fragment() {

    private var _binding: FragmentStationsListBinding? = null

    private val binding get() = _binding!!

    private lateinit var savedLocationsModel: SavedLocationsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStationsListBinding.inflate(inflater, container, false)

        savedLocationsModel = ViewModelProvider(requireActivity())[SavedLocationsViewModel::class.java]
        savedLocationsModel.locations.observe(viewLifecycleOwner) {
            val adapter = binding.stationsListView.adapter as SavedLocationsAdapter
            adapter.notifyChange(it!!)
        }


        binding.stationsListView.apply {
            adapter = SavedLocationsAdapter(context, savedLocationsModel,activity)
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