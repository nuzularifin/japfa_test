package com.dicoding.japfatest.domain.usecase

import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) : String {
        return userRepository.saveUserData(user)
    }
}