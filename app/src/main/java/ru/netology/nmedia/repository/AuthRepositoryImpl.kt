package ru.netology.nmedia.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService

import ru.netology.nmedia.dto.Token
//import ru.netology.nmedia.di.DependencyContainer

import ru.netology.nmedia.error.AuthError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.RegisterError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.RegisterModel
import java.io.IOException
import java.lang.RuntimeException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    //    @Inject
//    lateinit var appAuth: AppAuth
    override suspend fun updateUser(authData: AuthModel): Token {

        try {
            val response = apiService.updateUser(authData.login, authData.password)
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            val authResponse = response.body() ?: throw RuntimeException("body is null")

            if (authResponse.token != null) {
                //Token(id = authData.id, token = it, authData.avatar.orEmpty())
                // DependencyContainer.getInstance().appAuth.setAuth(id = authData.id, token = authData.token)
                //appAuth.setAuth(id = authData.id, token = authData.token)
                return Token(id = authResponse.id, token = authResponse.token, authResponse.avatar.orEmpty())
            } else {
                throw AuthError
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun register(registerData: RegisterModel): Token {
        try {

            val response = if (registerData.avatar == null) {
                apiService.registerUser(
                    registerData.login,
                    registerData.password,
                    registerData.name
                )
            } else {
                apiService.registerWithPhoto(
                    registerData.login.toRequestBody("text/plain".toMediaType()),
                    registerData.password.toRequestBody("text/plain".toMediaType()),
                    registerData.name.toRequestBody("text/plain".toMediaType()),
                    MultipartBody.Part.createFormData(
                        "file",
                        registerData.avatar.file.name,
                        registerData.avatar.file.asRequestBody()
                    )
                )
            }

            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            val authResponse = response.body() ?: throw RuntimeException("body is null")


            if (authResponse.token != null) {
                //Token(id = authData.id, token = it, authData.avatar.orEmpty())
                //DependencyContainer.getInstance().appAuth.setAuth(id = authData.id, token = authData.token)
                //appAuth.setAuth(id = authData.id, token = authData.token)
                return Token(
                    id = authResponse.id,
                    token = authResponse.token,
                    authResponse.avatar.orEmpty()
                )
            } else {
                throw RegisterError
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}
