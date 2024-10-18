package com.dicoding.japfatest.data.repository

import com.dicoding.japfatest.data.local.DataDao
import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.domain.repository.UserRepository
import com.dicoding.japfatest.utils.PreferenceHelper
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataDao: DataDao,
    private val preferenceHelper: PreferenceHelper,
) : UserRepository {
    override suspend fun saveUserData(userData: UserData) : String {
        try {
            dataDao.insertData(userData)
            return "success insert data"
        } catch (e: Exception) {
            return "failed insert data : $e"
        }
    }

    override suspend fun getUserData(): List<UserData> {
        return dataDao.getAllData()
    }

    override suspend fun deleteUserData(userData: UserData) : Boolean {
        try {
            dataDao.deleteData(userData)
            return true
        } catch (e: Exception){
            return false
        }
    }

//    override suspend fun checkUserExist(username: String, password: String): Boolean {
//        val result = dataDao.checkUserExist(username = username, password = password)
//        return result != null
//    }

    override suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        preferenceHelper.saveLoginStatus(isLoggedIn)
    }

    override fun getLoginStatus(): Boolean {
        return preferenceHelper.getLoginStatus()
    }
}