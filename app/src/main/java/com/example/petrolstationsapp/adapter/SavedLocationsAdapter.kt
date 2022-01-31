package com.example.petrolstationsapp.adapter

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.petrolstationsapp.database.MyDatabase
import com.example.petrolstationsapp.databinding.LocationListItemBinding
import com.example.petrolstationsapp.model.Location
import com.example.petrolstationsapp.viewmodel.SavedLocationsViewModel


class SavedLocationsAdapter(
    val context: Context,
    val savedLocationsModel: SavedLocationsViewModel,
    val activity: FragmentActivity?
) :
    RecyclerView.Adapter<SavedLocationsAdapter.LocationHolder>() {

    private var items: List<Location> = ArrayList()
    private lateinit var binding: LocationListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        binding = LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationHolder(binding, context, this, activity!!)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class LocationHolder(
        private var binding: LocationListItemBinding,
        private var context: Context,
        private var adapter: SavedLocationsAdapter,
        private var activity: FragmentActivity
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var item: Location

        fun bind(currentItem: Location) {
            this.item = currentItem
            binding.address.text = item.address
            binding.latitude.text = item.latitude.toString()
            binding.longitude.text = item.longitude.toString()
            binding.deleteButton.setOnClickListener {
                MyDatabase.getDatabase(context).locationDao().delete(item)
                adapter.savedLocationsModel.locations.value = adapter.savedLocationsModel.locations.value?.minus(item)
            }
            binding.mapButton.setOnClickListener {
                val data = Intent()
                data.putExtra("latitude", item.latitude)
                data.putExtra("longitude", item.longitude)
                data.putExtra("address", item.address)
                activity.setResult(RESULT_OK, data)
                activity.finish()
            }
        }
    }

    fun notifyChange(newList: List<Location>) {
        val diffResult = DiffUtil.calculateDiff(LocationListDiffCallback(items, newList))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

}
