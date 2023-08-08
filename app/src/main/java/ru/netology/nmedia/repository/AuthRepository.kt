package ru.netology.nmedia.repository


import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.RegisterModel


interface AuthRepository {


     suspend fun updateUser(authData: AuthModel)

     suspend fun register(registerData: RegisterModel)
}
