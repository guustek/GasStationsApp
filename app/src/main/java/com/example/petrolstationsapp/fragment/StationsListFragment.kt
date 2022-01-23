package com.example.petrolstationsapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.adapter.StationAdapter
import com.example.petrolstationsapp.databinding.FragmentStationsListBinding
import com.example.petrolstationsapp.viewmodel.StationViewModel


class StationsListFragment : Fragment() {

    private var _binding: FragmentStationsListBinding? = null

    private val binding get() = _binding!!

    private lateinit var stationsModel: StationViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStationsListBinding.inflate(inflater, container, false)

        stationsModel = ViewModelProvider(requireActivity())[StationViewModel::class.java]
        stationsModel.stations.observe(viewLifecycleOwner) {
            val adapter = binding.stationsListView.adapter as StationAdapter
            adapter.notifyChange(it!!)
        }


        binding.stationsListView.apply {
            adapter = StationAdapter(context)
            layoutManager = LinearLayoutManager(context)
        }
        binding.switchingButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.list_to_map)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}