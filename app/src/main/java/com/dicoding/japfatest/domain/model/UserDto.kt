package com.dicoding.japfatest.domain.model

import java.io.Serializable

data class UserDto(
    var id: Int? = 0,
    var fullName: String? = "",
    var gender: String? = "",
    var birthDate: String? = "",
    var address: String? = "",
    var dateTime: String? = "",
    var photoUri: String? = "",
    var latitude: String? = "",
    var longitude: String? = "",
) : Serializable {
    override fun toString(): String {
        return "UserDto(id=${id}, fullName=$fullName, gender=$gender, birthDate=$birthDate, address=$address, dateTime=$dateTime, photoUri=$photoUri, latitude=$latitude, longitude=$longitude)"
    }
}
