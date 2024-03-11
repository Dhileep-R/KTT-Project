package com.example.ktt_project.RealmConnection

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication:Application() {
    private lateinit var config:RealmConfiguration
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        config = RealmConfiguration.Builder().name("user.db").deleteRealmIfMigrationNeeded()
            .schemaVersion(0).allowWritesOnUiThread(true).build()
        config.let { Realm.setDefaultConfiguration(it) }
    }
}