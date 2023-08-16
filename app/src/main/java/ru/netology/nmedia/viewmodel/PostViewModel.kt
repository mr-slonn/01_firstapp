package ru.netology.nmedia.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth

import ru.netology.nmedia.dto.Post

import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.PostModel
import ru.netology.nmedia.model.PostModelState
import ru.netology.nmedia.repository.PostRepository

import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0L,
    likedByMe = false,
    likes = 0,
    published = "",
    shared = 0,
    viewsCount = 0,
    ownedByMe = false,
)

//private val noPhoto = PhotoModel()
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {


    private val _dataState = MutableLiveData(FeedModelState())
    private val _postState = MutableLiveData(PostModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val postState: LiveData<PostModelState>
        get() = _postState

//    val data: LiveData<FeedModel> = repository.data.map {
//        FeedModel(posts = it, empty = it.isEmpty())
//    }.asLiveData(Dispatchers.Default)

//    val data: LiveData<FeedModel> = AppAuth.getInstance()
//        .data
//        .flatMapLatest { (myId, _) ->
//            repository.data
//                .map { posts ->
//                    FeedModel(
//                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
//                        posts.isEmpty()
//                    )
//                }
//        }.asLiveData(Dispatchers.Default)

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val data: LiveData<FeedModel> = appAuth
//        .data
//        .flatMapLatest { token ->
//            repository.data.map { posts ->
//                FeedModel(
//                    posts = posts.map { it.copy(ownedByMe = it.authorId == token.id) },
//                    empty = posts.isEmpty()
//                )
//            }
//        }.asLiveData(Dispatchers.Default)

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = appAuth.data
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId)
                }
            }
        }


//    val data: Flow<PagingData<Post>> = appAuth.data
//        .flatMapLatest { (myId, _) ->
//            repository.data.map { post->
//                post.map { it.copy(ownedByMe = it.authorId==myId) }
//            }.flowOn(Dispatchers.Default)
//        }

//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            //.catch { e -> e.printStackTrace() }
//            //.asLiveData(Dispatchers.Default)
//            .asLiveData(Dispatchers.Default, 1000)
//    }


    //private val _photo = MutableLiveData(noPhoto)
    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

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
                _post.postValue(PostModel(post = post.copy(ownedByMe = it.authorId == appAuth.data.value.id)))
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
                val post = repository.getById(id)
                _post.postValue(PostModel(post.copy(ownedByMe = post.authorId == appAuth.data.value.id)))

                _postState.value = PostModelState()
            } catch (e: Exception) {
                _postState.value = PostModelState(error = true)
            }
        }
    }


    fun cancelEdit() {
        _edited.value = empty
    }

    //    fun save() {
//        _edited.value?.let {
//            viewModelScope.launch {
//                try {
//                    repository.save(it)
//                    _postCreated.value = Unit
//                    _dataState.value = FeedModelState()
//                    _edited.value = empty
//                } catch (e: Exception) {
//                    // _postCreated.value = FeedModelState(error = true)
//                    _postNotCreated.value = Unit
//                }
//            }
//        }
//    }
    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (val photo = _photo.value) {
                        null -> repository.save(it)
//                        else -> _photo.value?.file?.let { file ->
//                           // repository.saveWithAttachment(it, MediaUpload(file))
//                            repository.saveWithAttachment(it, file)
//                        }
                        else -> repository.saveWithAttachment(it, photo)
                    }
                    _dataState.value = FeedModelState()
                    _edited.value = empty
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                    _postNotCreated.value = Unit
                }
            }
        }
        edited.value = empty
        _photo.value = null
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

    //    fun changePhoto(uri: Uri, toFile: File) {
//        _photo.value = PhotoModel(uri, file)
//    }
    fun changePhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto() {
        _photo.value = null
        _edited.value = _edited.value?.copy(attachment = null)
    }
}
