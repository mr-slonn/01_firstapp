package ru.netology.nmedia.dao

import ru.netology.nmedia.dto.Post

interface PostDao {
    fun getAll(): List<Post>
//    fun getById(id: Long):Post?
    fun save(post: Post): Post
    fun likeById(id: Long)
    fun removeById(id: Long)
    fun sharedById(id:Long)
}
