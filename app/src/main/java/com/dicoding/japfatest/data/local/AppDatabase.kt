package com.dicoding.japfatest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.japfatest.data.model.UserData

@Database(entities = [UserData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao
}