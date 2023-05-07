package ru.netology.nmedia.repository

//import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    //fun getAll():LiveData<List<Post>>
    fun getAll(): List<Post>
    // fun getById(id: Long):LiveData<Post?>
    fun getById(id: Long): Post?
    fun likeById(id: Long): Post
    fun unLikeById(id: Long): Post
    fun sharedById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
}
