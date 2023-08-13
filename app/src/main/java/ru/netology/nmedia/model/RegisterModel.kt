package ru.netology.nmedia.model

data class RegisterModel(
    val login: String,
    val password: String,
    val name: String,
    val avatar: PhotoModel? = null
)
