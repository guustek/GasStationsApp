package com.example.petrolstationsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.petrolstationsapp.databinding.StationsListItemBinding
import com.example.petrolstationsapp.model.Station

class StationAdapter :
    RecyclerView.Adapter<StationAdapter.StationHolder>() {

    private var items: List<Station> = ArrayList()
    private lateinit var binding: StationsListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationHolder {
        binding = StationsListItemBinding.inflate(LayoutInflater.from(parent.context))
        return StationHolder(binding)
    }

    override fun onBindViewHolder(holder: StationHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class StationHolder(private var binding: StationsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var item: Station

        fun bind(currentItem: Station) {
            this.item = currentItem
            binding.name.text = if(item.name!=null) item.name else "Brak nazwy"
            binding.address.text = if(item.address!=null) item.address else "Brak adresu"
        }
    }

    fun notifyChange(newList: List<Station>) {
        val diffResult = DiffUtil.calculateDiff(StationListDiffCallback(items, newList))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }
}