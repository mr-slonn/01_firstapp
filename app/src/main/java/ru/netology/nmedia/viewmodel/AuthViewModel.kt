package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth

import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.RegisterModel
import ru.netology.nmedia.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

  // val authorized: Boolean
   //    get() = appAuth.data.value.token != null
//    val data: LiveData<Token> = appAuth.data.asLiveData()


    val data: LiveData<Token> = appAuth
        //.authStateFlow
        .data
        .asLiveData(Dispatchers.Default)
    val authorized: Boolean
        get() = appAuth.data.value.id != 0L


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
                val authResponse = repository.updateUser(authData)

                authResponse.let {
                    if (it.token != null) {
                        appAuth.setAuth(id = it.id, token = it.token)
                    }
                }


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
                        val authData = repository.register(registerData)
                        authData.let {
                            if (it.token != null) {
                                appAuth.setAuth(id = it.id, token = it.token)
                            }
                        }
                    }

                    else -> {
                        val authResponse = repository.register(registerData.copy(avatar = photo))
                        authResponse.let {
                            if (it.token != null) {
                                appAuth.setAuth(id = it.id, token = it.token)
                            }
                        }
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
