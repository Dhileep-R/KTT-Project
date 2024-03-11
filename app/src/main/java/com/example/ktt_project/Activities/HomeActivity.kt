package com.example.ktt_project.Activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ktt_project.Adapters.UsersLocationAdapter
import com.example.ktt_project.Models.DataModel
import com.example.ktt_project.Models.LocationData
import com.example.ktt_project.Services.Locationservice
import com.example.ktt_project.Interfaces.OnUserSelectedListener
import com.example.ktt_project.R
import com.example.ktt_project.PopUpWindows.UsersPopup
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where


class HomeActivity: AppCompatActivity(), OnUserSelectedListener {

    private lateinit var loggedInUser :TextView
    private lateinit var button :Button
    private var userId :String = ""

    private lateinit var realm: Realm
    private lateinit var recyclerView:RecyclerView

    private var service:Intent? = null


    private val backGroundLocation = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        when {
            it.getOrDefault(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startService(service)
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        when{
            it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION,false) -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        backGroundLocation.launch(arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                    }
                }
            }
            it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION,false) -> {

            }

        }
    }

    private val locationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "LOCATION_DATA_UPDATED") {
                displayLocationData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        service = Intent(this, Locationservice::class.java)

        checkPermissions()
        realm = Realm.getDefaultInstance()

        loggedInUser = findViewById(R.id.loggedInUser)
        button = findViewById(R.id.button)


        recyclerView = findViewById(R.id.recyclerView)


        userId = intent.getStringExtra("USERID_EXTRA").toString()
        fetchLoggedInUserDetails()



        button.setOnClickListener {
            val usersPopup = UsersPopup(this@HomeActivity,this@HomeActivity)
            usersPopup.showAsDropDown(button)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("LOCATION_DATA_UPDATED")
        LocalBroadcastManager.getInstance(this).registerReceiver(locationUpdateReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdateReceiver)
    }

    fun checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

                locationPermissions.launch(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION))
            }
            else{

                startService(service)
            }
        }
    }

    override fun onUserSelected(user: DataModel) {
        userId = user.id.toString()
        fetchLoggedInUserDetails()

    }

    private fun displayLocationData() {
        val locationDataList = realm.where<LocationData>().findAll().sort("timestamp",Sort.DESCENDING)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = UsersLocationAdapter(this,locationDataList)
        recyclerView.adapter = adapter
    }

    private fun fetchLoggedInUserDetails() {

        val CheckLoggedInUser = realm.where<DataModel>().equalTo("id", userId).findFirst()
        if (CheckLoggedInUser != null) {
            loggedInUser.text = "Welcome ${CheckLoggedInUser.username}"
        }
    }
}