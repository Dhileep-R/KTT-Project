package com.example.ktt_project.Models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DataModel : RealmObject() {
    @PrimaryKey
    var id: String? = null;

    var username: String? = null
    var email: String? = null
    var password: String? = null

}
