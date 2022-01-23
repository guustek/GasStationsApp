package com.example.petrolstationsapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.petrolstationsapp.model.Station

class StationListDiffCallback(private var oldList: List<Station>, private var newList: List<Station>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition]==newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition]==newList[newItemPosition])
    }
}