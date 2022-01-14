package com.example.petrolstationsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.model.PetrolStation

class PetrolStationAdapter(private var items: List<PetrolStation>) :
    RecyclerView.Adapter<PetrolStationAdapter.PetrolStationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetrolStationViewHolder {
        val itemVIew = LayoutInflater.from(parent.context).inflate(R.layout.stations_list_item, parent, false)
        return PetrolStationViewHolder(itemVIew)
    }

    override fun onBindViewHolder(holder: PetrolStationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PetrolStationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var item: PetrolStation
        private var textView: TextView
        private var buttonView: Button

        init {
            textView = itemView.findViewById(R.id.name)
            buttonView = itemView.findViewById(R.id.button)
        }

        fun bind(currentItem: PetrolStation) {
            this.item = currentItem
            textView.text=currentItem.name
        }
    }
}