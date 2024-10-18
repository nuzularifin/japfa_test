package com.dicoding.japfatest.domain.usecase

import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(userData: UserData) : Boolean {
        return userRepository.deleteUserData(userData)
    }
}