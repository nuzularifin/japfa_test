package com.dicoding.japfatest.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.domain.model.UserDto
import com.dicoding.japfatest.domain.usecase.DeleteUserDataUseCase
import com.dicoding.japfatest.domain.usecase.GetAllUserDataUseCase
import com.dicoding.japfatest.domain.usecase.GetLoginStatusUseCase
import com.dicoding.japfatest.domain.usecase.SaveLoginStatusUseCase
import com.dicoding.japfatest.domain.usecase.SaveUserUseCase
import com.dicoding.japfatest.utils.Constans
import com.dicoding.japfatest.utils.UiState
import com.dicoding.japfatest.utils.toUserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase,
    private val saveLoginStatusUseCase: SaveLoginStatusUseCase,
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val getAllUserDataUseCase: GetAllUserDataUseCase,
    private val deleteUserDataUseCase: DeleteUserDataUseCase,
) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _usersData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _usersData

    private val _saveUserData = MutableLiveData<UiState<Boolean>>()
    val saveUserData: LiveData<UiState<Boolean>> = _saveUserData

    private val _getUsersData = MutableLiveData<UiState<List<UserDto>>>()
    val getUserData: LiveData<UiState<List<UserDto>>> = _getUsersData

    private val _deleteUserData = MutableLiveData<UiState<Boolean>>()
    val deleteUserData: LiveData<UiState<Boolean>> = _deleteUserData

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String){
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000L)
            if (username.isEmpty() || password.isEmpty()){
                _message.value = "Username atau Password tidak boleh kosong"
                _isLoggedIn.value = false
            } else if (username == Constans.ADMIN_USERNAME && password == Constans.ADMIN_PASSWORD){
                _message.value = "Success Login"
                saveLoginStatus(isLoggedIn = true)
                _isLoggedIn.value = true
            } else {
                _message.value = "Username / password tidak ditemukan"
                _isLoggedIn.value = false
            }
            _isLoading.value = false
        }
    }

    fun saveUserData(userData: UserData) {
        viewModelScope.launch {
            _saveUserData.value = UiState.Loading(true)
            when {
                userData.fullName?.isEmpty() == true -> {
                    _saveUserData.value = UiState.Loading(false)
                    _saveUserData.value = UiState.Error("Nama lengkap tidak boleh kosong")
                }
                userData.gender?.isEmpty() == true -> {
                    _saveUserData.value = UiState.Loading(false)
                    _saveUserData.value = UiState.Error(message = "Jenis kelamin tidak boleh kosong")
                }
                userData.birthDate?.isEmpty() == true -> {
                    _saveUserData.value = UiState.Loading(false)
                    _saveUserData.value = UiState.Error("Tanggal lahir tidak boleh kosong")
                }
                userData.address?.isEmpty() == true -> {
                    _saveUserData.value = UiState.Loading(false)
                    _saveUserData.value = UiState.Error("Alamat tidak boleh kosong")
                }
                userData.dateTime?.isEmpty() == true -> {
                    _saveUserData.value = UiState.Loading(false)
                    _saveUserData.value = UiState.Error("Tanggal dan waktu tidak boleh kosong")
                }
                userData.latitude?.isEmpty() == true || userData.longitude?.isEmpty() == true -> {
                    _saveUserData.value = UiState.Loading(false)
                    _saveUserData.value = UiState.Error("Lokasi tidak ditemukan")
                }
                userData.photoUri?.isEmpty() == true -> {
                    _saveUserData.value = UiState.Loading(false)
                    _saveUserData.value = UiState.Error("Foto tidak boleh kosong")
                }
                else -> {
                    delay(1000L)
                    saveUserUseCase(userData)
                    _saveUserData.value = UiState.Success(true)
                }
            }
        }
    }

    fun deleteUserData(userData: UserData) {
        viewModelScope.launch {
            _deleteUserData.value = UiState.Loading(isLoading = true)
            val result = deleteUserDataUseCase(userData = userData)
            if (result){
                _deleteUserData.value = UiState.Success(true)
            } else {
                _deleteUserData.value = UiState.Error("Gagal menghapus data")
            }
            _deleteUserData.value = UiState.Loading(isLoading = false)
        }
    }

    fun getAllDataUser(){
        viewModelScope.launch {
            _getUsersData.value = UiState.Loading(isLoading = true)
            try {
                val result = getAllUserDataUseCase.invoke()
                _getUsersData.value = UiState.Success(result.map { it.toUserDto() })
            } catch (e: Exception){
                _getUsersData.value = UiState.Error(message = e.message ?: "Gagal mendapatkan data")
            }
            _getUsersData.value = UiState.Loading(isLoading = false)
        }
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        viewModelScope.launch {
            saveLoginStatusUseCase(isLoggedIn)
        }
    }

    fun getLoginStatus() : Boolean {
        return getLoginStatusUseCase()
    }
}