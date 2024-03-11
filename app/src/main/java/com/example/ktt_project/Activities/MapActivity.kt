package com.example.ktt_project.Activities

import android.os.Bundle
import android.os.Handler

import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import com.example.ktt_project.Adapters.InfoWindowAdapter
import com.example.ktt_project.Models.LocationData
import com.example.ktt_project.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.kotlin.where


class MapActivity:AppCompatActivity(),OnMapReadyCallback {

    private var mGoogleMap:GoogleMap? = null

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var position:Int = 0;
    private lateinit var playbackButton: Button
    private var isPlaybackRunning = false
    private var playbackSpeed = 1000L
    private var currentLocationIndex = 0
    private var locationHistory: List<LocationData> = emptyList()
    private val playbackHandler = Handler()
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        position = intent.getIntExtra("Position",0)

        playbackButton = findViewById(R.id.playbackButton)



        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        realm = Realm.getDefaultInstance()

        playbackButton.setOnClickListener {
            locationHistory = realm.where<LocationData>().findAll()
            if (isPlaybackRunning) {
                stopPlayback()
            } else {
                startPlayback()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap
        val location = LatLng(latitude, longitude)
        mGoogleMap!!.addMarker(MarkerOptions().position(location).title(position.toString()))
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(12f))

        val infoWindowAdapter = InfoWindowAdapter(this)
        mGoogleMap!!.setInfoWindowAdapter(infoWindowAdapter)
    }

    private fun startPlayback() {
        if (locationHistory.isNotEmpty()) {
            isPlaybackRunning = true
            playbackButton.text = "Stop Playback"
            currentLocationIndex = position
            playbackHandler.postDelayed(playbackRunnable, playbackSpeed)
        }
    }

    private fun stopPlayback() {
        isPlaybackRunning = false
        playbackButton.text = "Start Playback"
        playbackHandler.removeCallbacks(playbackRunnable)
        locationHistory = emptyList()
    }

    private val playbackRunnable = object : Runnable {
        override fun run() {
            if(currentLocationIndex == 0){
                stopPlayback()
            }
            else if (currentLocationIndex < locationHistory.size) {
                val location = locationHistory[currentLocationIndex]
                val latLng = LatLng(location.latitude!!, location.longitude!!)
                mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                mGoogleMap?.addMarker(MarkerOptions().position(latLng).title("LocationIndex " + currentLocationIndex.toString()))
                mGoogleMap?.animateCamera(CameraUpdateFactory.zoomTo(20f))

                val infoWindowAdapter = InfoWindowAdapter(applicationContext
                )
                mGoogleMap?.setInfoWindowAdapter(infoWindowAdapter)

                currentLocationIndex--
                playbackHandler.postDelayed(this, playbackSpeed)
            } else {
                stopPlayback()
            }
        }
    }

}