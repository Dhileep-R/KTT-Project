package com.example.ktt_project.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ktt_project.Models.LocationData
import com.example.ktt_project.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.realm.Realm
import java.util.Date
import java.util.UUID

class Locationservice : Service() {

    private var fusedLocationProviderClient:FusedLocationProviderClient? = null
    private var locationCallback:LocationCallback? = null
    private var locationRequest:LocationRequest? = null
    private var location: Location? = null

    private var notificationManager:NotificationManager? = null

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,15*60*1000).setIntervalMillis(15*60*1000).build()
        locationCallback = object :LocationCallback(){
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
            }

            @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }

        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel("12345","location",NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(notificationChannel)
        }

    }

    @Suppress("MissingPermission")
    fun createLocationRequest(){
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!,locationCallback!!,null
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { bgRealm ->
                val locationData = bgRealm.createObject(LocationData::class.java, UUID.randomUUID().toString())
                locationData.latitude = location?.latitude
                locationData.longitude = location?.longitude
                locationData.timestamp = Date()
            }
        }
        val intent = Intent("LOCATION_DATA_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        startForeground(12345,getNotification())
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun getNotification(): Notification {
        val notification = NotificationCompat.Builder(this,"12345")
            .setContentTitle("Location Updates")
            .setContentText("Latitude--> ${location?.latitude}\nLongitude--> ${location?.longitude}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notification.setChannelId("12345")
        }
        return notification.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createLocationRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder ? = null

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }
}

