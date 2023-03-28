package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
//import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.db.AppDbRoom
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl

//import ru.netology.nmedia.repository.PostRepositorySQLiteImpl
//import ru.netology.nmedia.repository.PostRepositorySharedPrefsImpl
//import ru.netology.nmedia.repository.PostRepositoryFileImpl
//import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    shared = 0,
    viewsCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {


    // private val repository: PostRepository = PostRepositoryInMemoryImpl()
    // private val repository: PostRepository = PostRepositorySharedPrefsImpl(application)

    // private val repository: PostRepository = PostRepositoryFileImpl(application)

    // private val repository: PostRepository = PostRepositorySQLiteImpl(
    //      AppDb.getInstance(application).postDao
    // )

    private val repository: PostRepository = PostRepositoryRoomImpl(
        AppDbRoom.getInstance(context = application).postDaoRoom()
    )

    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun likeById(id: Long) = repository.likeById(id)
    fun sharedById(id: Long) = repository.sharedById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun getPost(id: Long): Post? {
        // TODO: не помнимою как открывать новый пост запросом из базы

        return data.value?.firstOrNull { it.id == id }
            ?.copy()
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


}
