package com.example.petrolstationsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petrolstationsapp.databinding.StationsListItemBinding
import com.example.petrolstationsapp.model.PetrolStation
import com.example.petrolstationsapp.utils.DataParser

class PetrolStationAdapter(private var items: List<PetrolStation>) :
    RecyclerView.Adapter<PetrolStationAdapter.PetrolStationViewHolder>() {
    private lateinit var binding:StationsListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetrolStationViewHolder {
        binding = StationsListItemBinding.inflate(LayoutInflater.from(parent.context))
        return PetrolStationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetrolStationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PetrolStationViewHolder(private var binding: StationsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var item: PetrolStation

        fun bind(currentItem: PetrolStation) {
            this.item = currentItem
            binding.name.text=item.name
            binding.address.text= DataParser.getAddressFromCoordinates(item.location,binding.address.context)
        }
    }
}