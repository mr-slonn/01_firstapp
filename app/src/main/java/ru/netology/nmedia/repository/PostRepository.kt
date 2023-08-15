package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {

   // val data: Flow<List<Post>>

    val data: Flow<PagingData<Post>>

    suspend fun getAll()

    suspend fun showAll()

    // fun getById(id: Long):LiveData<Post?>
    //suspend fun getById(id: Long):Post?
    suspend fun getById(id: Long): Post
    suspend fun likeById(id: Long): Post
    suspend fun unLikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun getAttachmentUrl(fileName: String): String
    suspend fun getAvatarUrl(fileName: String): String
    fun getNewerCount(id: Long): Flow<Int>

    suspend fun saveWithAttachment(post: Post, upload: PhotoModel)
    suspend fun upload(upload: PhotoModel): Media


}
