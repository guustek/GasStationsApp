package com.example.petrolstationsapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.database.MyDatabase
import com.example.petrolstationsapp.databinding.StationListItemBinding
import com.example.petrolstationsapp.model.Station


class StationAdapter(val context: Context) :
    RecyclerView.Adapter<StationAdapter.StationHolder>() {

    private var items: List<Station> = ArrayList()
    private lateinit var binding: StationListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationHolder {
        binding = StationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StationHolder(binding, context, this)
    }

    override fun onBindViewHolder(holder: StationHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class StationHolder(
        private var binding: StationListItemBinding,
        private var context: Context,
        private var adapter: StationAdapter
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var item: Station

        @SuppressLint("MissingPermission")
        fun bind(currentItem: Station) {
            this.item = currentItem
            val savedStations = MyDatabase.getDatabase(context).stationDao().getAll()
            binding.includedLayout.favoriteButton.setImageResource(R.drawable.not_favorite_30x30)
            if (savedStations?.contains(this.item) == true)
                binding.includedLayout.favoriteButton.setImageResource(R.drawable.favorite_30x30)
            binding.includedLayout.name.text = item.name
            if (item.rating == 0f) binding.includedLayout.rating.isVisible =
                false else binding.includedLayout.rating.rating = item.rating!!
            if (item.ratingsCount == 0) binding.includedLayout.ratingsCount.isVisible =
                false else binding.includedLayout.ratingsCount.text = "(${item.ratingsCount})"
            binding.includedLayout.address.text = item.location.address
            if (item.isOpenNow == null)
                binding.includedLayout.isOpenNow.isVisible = false
            else if (item.isOpenNow!!) {
                binding.includedLayout.isOpenNow.setTextColor(Color.GREEN)
                binding.includedLayout.isOpenNow.text = context.getString(R.string.open)
            } else {
                binding.includedLayout.isOpenNow.setTextColor(Color.RED)
                binding.includedLayout.isOpenNow.text = context.getString(R.string.closed)
            }

            binding.includedLayout.directionButton.setOnClickListener {
                val uri = Uri.parse("google.navigation:q=${item.location.latitude},${item.location.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(context, mapIntent, null)
            }
            binding.includedLayout.favoriteButton.setOnClickListener {
                val station = savedStations?.find { it == item }
                if (station == null)
                    MyDatabase.getDatabase(context).stationDao().insert(this.item)
                else
                    MyDatabase.getDatabase(context).stationDao().delete(station)
                adapter.notifyItemChanged(layoutPosition)
            }
        }
    }

    fun notifyChange(newList: List<Station>) {
        val diffResult = DiffUtil.calculateDiff(StationListDiffCallback(items, newList))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }
}