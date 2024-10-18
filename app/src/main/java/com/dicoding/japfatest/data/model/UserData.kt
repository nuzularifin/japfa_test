package com.dicoding.japfatest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var fullName: String? = "",
    var gender: String? = "",
    val birthDate: String? = "",
    var address: String? = "",
    var dateTime: String? = "",
    var photoUri: String? = "",
    var latitude: String? = "",
    var longitude: String? = "",
)
