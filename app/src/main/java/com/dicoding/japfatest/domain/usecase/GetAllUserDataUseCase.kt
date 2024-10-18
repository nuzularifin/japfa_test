package com.dicoding.japfatest.domain.usecase

import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.domain.repository.UserRepository
import javax.inject.Inject

class GetAllUserDataUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() : List<UserData> {
        return repository.getUserData()
    }
}