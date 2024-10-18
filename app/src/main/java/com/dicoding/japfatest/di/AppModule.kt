package com.dicoding.japfatest.di

import android.content.Context
import androidx.room.Room
import com.dicoding.japfatest.data.local.AppDatabase
import com.dicoding.japfatest.data.local.DataDao
import com.dicoding.japfatest.domain.repository.UserRepository
import com.dicoding.japfatest.data.repository.UserRepositoryImpl
import com.dicoding.japfatest.domain.usecase.DeleteUserDataUseCase
import com.dicoding.japfatest.domain.usecase.GetAllUserDataUseCase
import com.dicoding.japfatest.domain.usecase.SaveUserUseCase
import com.dicoding.japfatest.utils.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "user_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePreferenceHelper(@ApplicationContext context: Context): PreferenceHelper {
        return PreferenceHelper(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): DataDao {
        return appDatabase.dataDao()
    }

    @Provides
    @Singleton
    fun providesRepository(dataDao: DataDao, preferenceHelper: PreferenceHelper): UserRepository {
        return UserRepositoryImpl(dataDao, preferenceHelper)
    }

    @Provides
    @Singleton
    fun providesUserUseCase(userRepository: UserRepository): SaveUserUseCase {
        return SaveUserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun providesGetAllUserData(userRepository: UserRepository) : GetAllUserDataUseCase {
        return GetAllUserDataUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun providesDeleteUserData(userRepository: UserRepository) : DeleteUserDataUseCase {
        return DeleteUserDataUseCase(userRepository)
    }
}