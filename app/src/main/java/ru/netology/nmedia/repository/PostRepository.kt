package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll():LiveData<List<Post>>
    fun likeById(id: Long)
    fun sharedById(id:Long,shared: Int)
}
