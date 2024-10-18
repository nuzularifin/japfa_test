package com.dicoding.japfatest.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.japfatest.data.model.UserData

@Dao
interface DataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(userData: UserData)

    @Query("SELECT * FROM user_data")
    suspend fun getAllData(): List<UserData>

    @Delete
    suspend fun deleteData(userData: UserData)

//    @Query("SELECT * FROM user_data WHERE fullName = :username AND password = :password")
//    suspend fun checkUserExist(username: String, password: String): UserData?
}