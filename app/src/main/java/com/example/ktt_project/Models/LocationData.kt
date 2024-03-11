package com.example.ktt_project.Models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

open class LocationData : RealmObject() {
    var latitude: Double? = null
    var longitude: Double? = null
    var timestamp: Date = Date()

    @PrimaryKey
    var userId: String? = ""
}