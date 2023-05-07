package ru.netology.nmedia.viewmodel

import android.app.Application
//import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.db.AppDbRoom
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.PostModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent


import java.io.IOException
import kotlin.concurrent.thread

//import ru.netology.nmedia.repository.PostRepositoryRoomImpl
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

//    private val repository: PostRepository = PostRepositoryRoomImpl(
//        AppDbRoom.getInstance(context = application).postDaoRoom()
//    )


    private val repository: PostRepository = PostRepositoryImpl()

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _post = SingleLiveEvent<PostModel>()
    val post: LiveData<PostModel>
        get() = _post

    //var post:Post? = null

    // TODO: добавлено для вебинара
    init {
        loadPosts()
    }

    fun backPost() {
        _post.postValue(PostModel(post = null))
    }

    fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue) // в вебинаре .let
        }
    }


    //TODO:  закрыто для вебинара
    //val data = repository.getAll()


    // вебинаре по другому
    // fun likeById(id: Long) = repository.likeById(id)
    fun likeById(id: Long) {

        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .map {
                    if (it.id != id) it else it.copy(
                        likedByMe = !it.likedByMe,
                        likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                    )
                }
            )
        )

        thread {
            val post = repository.likeById(id)
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        if (it.id != id) it else post
                    }
                )
            )
        }
    }

    fun unLikeById(id: Long) {

        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .map {
                    if (it.id != id) it else it.copy(
                        likedByMe = !it.likedByMe,
                        likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                    )
                }
            )
        )

        thread {
            val post = repository.unLikeById(id)
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        if (it.id != id) it else post
                    }
                )
            )
        }
    }

    fun likeByIdFromPost(id: Long) {
        _post.postValue(
            _post.value?.copy(
                post = _post.value?.post?.copy(
                    likedByMe = !_post.value?.post!!.likedByMe,
                    likes = if (_post.value?.post!!.likedByMe) _post.value?.post!!.likes - 1 else _post.value?.post!!.likes + 1
                )
            )
        )
        thread {
            val post = repository.likeById(id)
            _post.postValue(
                _post.value?.copy(post = post)
            )
        }
    }

    fun unLikeByIdFromPost(id: Long) {
        _post.postValue(
            _post.value?.copy(
                post = _post.value?.post?.copy(
                    likedByMe = !_post.value?.post!!.likedByMe,
                    likes = if (_post.value?.post!!.likedByMe) _post.value?.post!!.likes - 1 else _post.value?.post!!.likes + 1
                )
            )
        )
        thread {
            val post = repository.unLikeById(id)
            _post.postValue(
                _post.value?.copy(post = post)
            )
        }
    }

    fun sharedById(id: Long) = repository.sharedById(id)

    // fun removeById(id: Long) = repository.removeById(id)
    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun getPost(id: Long) {
        //  post = repository.getById(id)
        thread {
            // Начинаем загрузку
            _post.postValue(PostModel(loading = true))
            try {
                // Данные успешно получены
                val post = repository.getById(id)
                PostModel(post = post, empty = post == null)
            } catch (e: IOException) {
                // Получена ошибка
                PostModel(error = true)
            }.also(_post::postValue) // в вебинаре .let
        }

    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun save() {
        //thread из вебинара
        thread {
            edited.value?.let {
                thread {
                    repository.save(it)
                    _postCreated.postValue(Unit)
                }
            }
            // это заменили в вэбинаре
            // edited.value = empty
            edited.postValue(empty)
        }
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
