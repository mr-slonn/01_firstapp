package ru.netology.nmedia.repository


import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.RegisterModel


interface AuthRepository {


    suspend fun updateUser(authData: AuthModel): Token

    suspend fun register(registerData: RegisterModel): Token
}
