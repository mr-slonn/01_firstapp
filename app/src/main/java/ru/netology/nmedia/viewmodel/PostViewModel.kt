package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PostModel
import ru.netology.nmedia.model.PostModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent


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
    private val repository: PostRepository =
        PostRepositoryImpl(
            AppDb.getInstance(application).postDao()
        )

    private val _dataState = MutableLiveData(FeedModelState())
    private val _postState = MutableLiveData(PostModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val postState: LiveData<PostModelState>
        get() = _postState

    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(posts = it, empty = it.isEmpty())
    }.asLiveData(Dispatchers.Default)

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            //.catch { e -> e.printStackTrace() }
           //.asLiveData(Dispatchers.Default)
            .asLiveData(Dispatchers.Default, 1000)
    }





    private val _edited = MutableLiveData(empty)
    val edited
        get() = _edited

    private val _postCreated = SingleLiveEvent<Unit>()
    private val _postNotCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val postNotCreated: LiveData<Unit>
        get() = _postNotCreated

    private val _post = SingleLiveEvent<PostModel>()
    val post: LiveData<PostModel>
        get() = _post

    init {
        loadPosts()
    }

    fun backPost() {
        _post.postValue(PostModel(post = null))
        _postState.value = PostModelState()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.getAll()
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(smallError = true)
        }
    }

    fun refreshPost() = viewModelScope.launch {
        try {
            _postState.value = PostModelState(refreshing = true)
            _post.value?.post?.let {
                val post = repository.getById(it.id)
                _post.postValue(PostModel(post = post))
            }
            _postState.value = PostModelState()
        } catch (e: Exception) {
            _postState.value = PostModelState(smallError = true)
        }
    }

    fun likeByIdV2(post: Post) {
        viewModelScope.launch {
            try {
                if (!post.likedByMe) {
                    repository.likeById(post.id)
                } else {
                    repository.unLikeById(post.id)
                }
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(smallError = true)
            }
        }
    }

    fun likeByIdFromPostV2() {
        _post.value?.post?.let {
            viewModelScope.launch {
                try {
                    if (!it.likedByMe) {
                        _post.postValue(PostModel(repository.likeById(it.id)))
                    } else {
                        _post.postValue(PostModel(repository.unLikeById(it.id)))
                    }
                    _postState.value = PostModelState()
                } catch (e: Exception) {
                    _postState.value = PostModelState(smallError = true)
                }
            }

        }
    }


    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(smallError = true)
            }
        }
    }

    fun getById(id: Long) {
        viewModelScope.launch {
            try {
                _postState.value = PostModelState(loading = true)

                _post.postValue(PostModel(repository.getById(id)))

                _postState.value = PostModelState()
            } catch (e: Exception) {
                _postState.value = PostModelState(error = true)
            }
        }
    }


    fun cancelEdit() {
        _edited.value = empty
    }

    fun save() {
        _edited.value?.let {
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _postCreated.value = Unit
                    _dataState.value = FeedModelState()
                    _edited.value = empty
                } catch (e: Exception) {
                    // _postCreated.value = FeedModelState(error = true)
                    _postNotCreated.value = Unit
                }
            }
        }
    }

    fun edit(post: Post) {
        _edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (_edited.value?.content == text) {
            return
        }
        _edited.value = _edited.value?.copy(content = text)
    }


    fun showHiddenPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.showAll()
            _dataState.value = FeedModelState()
        } catch (e: java.lang.Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}
