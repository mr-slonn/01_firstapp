package ru.netology.nmedia.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth

import ru.netology.nmedia.error.AuthError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.RegisterError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.RegisterModel
import java.io.IOException
import java.lang.RuntimeException

class AuthRepositoryImpl : AuthRepository {
    override suspend fun updateUser(authData: AuthModel) {

         try {
            val response = PostsApi.retrofitService.updateUser(authData.login, authData.password)
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            val authData = response.body() ?: throw RuntimeException("body is null")

           if (authData.token != null) {
               //Token(id = authData.id, token = it, authData.avatar.orEmpty())
               AppAuth.getInstance().setAuth(id = authData.id, token = authData.token)
           } else
           {
               throw AuthError
           }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun register(registerData: RegisterModel) {
         try {

            val response = if (registerData.avatar == null) {
                PostsApi.retrofitService.registerUser(
                    registerData.login,
                    registerData.password,
                    registerData.name
                )
            } else {
                PostsApi.retrofitService.registerWithPhoto(
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
            val authData = response.body() ?: throw RuntimeException("body is null")


             if (authData.token != null) {
                 //Token(id = authData.id, token = it, authData.avatar.orEmpty())
                 AppAuth.getInstance().setAuth(id = authData.id, token = authData.token)
             } else
             {
                 throw RegisterError
             }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}
