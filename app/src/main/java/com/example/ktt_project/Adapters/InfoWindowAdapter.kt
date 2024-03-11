package com.example.ktt_project.Adapters

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import com.example.ktt_project.databinding.CustomInfoWindowLayoutBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class InfoWindowAdapter(context: Context):
GoogleMap.InfoWindowAdapter{

    private val binding:CustomInfoWindowLayoutBinding = CustomInfoWindowLayoutBinding.inflate(
    LayoutInflater.from(context),null,false)

    override fun getInfoContents(p0: Marker): View? {
        TODO("Not yet implemented")
    }

    override fun getInfoWindow(marker: Marker): View? {
        binding.infoWindowText.text = marker.title
        binding.infoWindowPosition.text = marker.position.toString()

        return binding.root
    }
}


