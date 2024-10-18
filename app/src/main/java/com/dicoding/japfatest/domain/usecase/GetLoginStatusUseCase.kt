package com.dicoding.japfatest.domain.usecase

import com.dicoding.japfatest.domain.repository.UserRepository
import javax.inject.Inject

class GetLoginStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Boolean {
        return userRepository.getLoginStatus()
    }
}