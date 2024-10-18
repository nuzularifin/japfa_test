package com.dicoding.japfatest.domain.usecase

import com.dicoding.japfatest.domain.repository.UserRepository
import javax.inject.Inject

class SaveLoginStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(isLoggedIn: Boolean) {
        userRepository.saveLoginStatus(isLoggedIn)
    }

}