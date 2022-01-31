package com.example.petrolstationsapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.petrolstationsapp.model.Location

class LocationListDiffCallback(private var oldList: List<Location>, private var newList: List<Location>) :
    DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition] == newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition] == newList[newItemPosition])
    }
}