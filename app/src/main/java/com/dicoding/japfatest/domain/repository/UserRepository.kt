package com.dicoding.japfatest.domain.repository

import com.dicoding.japfatest.data.model.UserData

interface UserRepository {
    suspend fun saveUserData(userData: UserData) : String
    suspend fun getUserData(): List<UserData>
    suspend fun deleteUserData(userData: UserData) : Boolean

    suspend fun saveLoginStatus(isLoggedIn: Boolean)
    fun getLoginStatus() : Boolean
}