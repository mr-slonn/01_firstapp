package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth

import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.RegisterModel
import ru.netology.nmedia.repository.AuthRepository
import ru.netology.nmedia.repository.AuthRepositoryImpl


class AuthViewModel (application: Application) : AndroidViewModel(application) {

    val authorized: Boolean
        get() = AppAuth.getInstance().data.value.token != null
    val data: LiveData<Token> = AppAuth.getInstance().data.asLiveData()


//    val data: LiveData<AuthState> = AppAuth.getInstance()
//        .authStateFlow
//        .asLiveData(Dispatchers.Default)
//    val authenticated: Boolean
//        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    private val repository: AuthRepository =
        AuthRepositoryImpl()

    private val _authState = MutableLiveData(AuthModelState())
    val authState: LiveData<AuthModelState>
        get() = _authState

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

    fun changePhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto() {
        _photo.value = null
    }


    fun updateUser(authData: AuthModel) {
        viewModelScope.launch {
            try {
                _authState.value = AuthModelState(loading = true)
                repository.updateUser(authData)
                _authState.value = AuthModelState()
            } catch (e: Exception) {
                _authState.value = AuthModelState(smallError = true)

            }
        }
    }

    fun register(registerData: RegisterModel) {

        viewModelScope.launch {
            try {
                _authState.value = AuthModelState(loading = true)
                when (val photo = _photo.value) {
                    null -> {
                        repository.register(registerData)
                    }
                    else -> {
                        repository.register(registerData.copy(avatar = photo))
                    }
                }
                _authState.value = AuthModelState()
                _photo.value = null
            } catch (e: Exception) {
                _authState.value = AuthModelState(smallError = true)
            }
        }
    }
}
