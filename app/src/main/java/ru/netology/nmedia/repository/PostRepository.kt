package ru.netology.nmedia.repository

//import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    //fun getAll():LiveData<List<Post>>
//    fun getAll(): List<Post>
    fun getAll(callback: PostsCallback<List<Post>>)

    // fun getById(id: Long):LiveData<Post?>
    fun getById(id: Long, callback: PostsCallback<Post?>)
    fun likeById(id: Long, callback: PostsCallback<Post>)
    fun unLikeById(id: Long, callback: PostsCallback<Post>)
    fun sharedById(id: Long)
    fun removeById(id: Long, callback: PostsCallback<Unit>)
    fun save(post: Post, callback: PostsCallback<Post>)
    fun getAttachmentUrl(fileName: String): String
    fun getAvatarUrl(fileName: String): String

    interface PostsCallback<T> {
        fun onSuccess(data: T) {}
        fun onError(e: Exception) {}
    }
}
