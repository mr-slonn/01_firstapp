package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 0,
    var likedByMe: Boolean = false,
    var shared: Int = 0,
    var viewsCount: Int =0,
    var video: String ? = null,
    )
