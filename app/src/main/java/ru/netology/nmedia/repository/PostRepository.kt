package ru.netology.nmedia.repository

//import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data: LiveData<List<Post>>

    suspend fun getAll()

    // fun getById(id: Long):LiveData<Post?>
    //suspend fun getById(id: Long):Post?
    suspend fun getById(id: Long): Post
    suspend fun likeById(id: Long): Post
    suspend fun unLikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun getAttachmentUrl(fileName: String): String
    suspend fun getAvatarUrl(fileName: String): String


}
