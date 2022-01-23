package com.example.petrolstationsapp.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.databinding.StationInfoBinding
import com.google.android.gms.maps.model.Marker

class InfoWindowFragment(private val marker: Marker, private val isNightMode: Boolean) : Fragment() {

    private var _binding: StationInfoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StationInfoBinding.inflate(inflater, container, false)
        val title = marker.title
        val snippet = marker.snippet!!.split(';')
        val rating = snippet[0].toFloat()
        val address = snippet[1]
        val isOpenNow = if (snippet[2] == "null") null else snippet[2].toBoolean()
        val ratingsCount = snippet[3].toInt()

        binding.name.text = title
        if (rating == 0f) binding.rating.isVisible = false else binding.rating.rating = rating
        if (ratingsCount == 0) binding.ratingsCount.isVisible = false else binding.ratingsCount.text = "($ratingsCount)"
        binding.address.text = address
        if (isOpenNow == null)
            binding.isOpenNow.isVisible = false
        else if (isOpenNow) {
            binding.isOpenNow.setTextColor(Color.GREEN)
            binding.isOpenNow.text = "Teraz otwarte"
        } else {
            binding.isOpenNow.setTextColor(Color.RED)
            binding.isOpenNow.text = "Teraz zamknięte"
        }
        binding.linearLayout.updateLayoutParams {
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
        }
        binding.directionButton.setOnClickListener {
            val uri = Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            ContextCompat.startActivity(context!!, mapIntent, null)
        }
        if (isNightMode)
            binding.root.setBackgroundColor(resources.getColor(R.color.cardview_dark_background))
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}