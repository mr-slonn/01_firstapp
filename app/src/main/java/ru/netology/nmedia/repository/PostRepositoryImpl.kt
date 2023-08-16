package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.CancellationException

import kotlinx.coroutines.delay

import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import okio.IOException
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.lang.RuntimeException
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.model.PhotoModel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PostRepositoryImpl @Inject constructor(
    appDb: AppDb,
    private val dao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: ApiService
) : PostRepository {

//    override val data: Flow<List<Post>> = dao.getAll().map {
//        it.map(PostEntity::toDto)
//    }

//    override val data: Flow<PagingData<Post>> = Pager(
//        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
//        pagingSourceFactory = {
//            PostPagingSource(
//                apiService
//            )
//                              },
//    ).flow

//    override val data: Flow<PagingData<Post>> = Pager(
//        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
//        pagingSourceFactory = { PostPagingSource(apiService) },
//    ).flow

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 1, enablePlaceholders = false),
        pagingSourceFactory = { dao.pagingSource() },
        remoteMediator = PostRemoteMediator(
            service = apiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            db = appDb
        )
    ).flow.map {
        // it.map { it.toDto() }
        it.map(PostEntity::toDto)
            .insertSeparators { previous, next ->
                if (previous?.id?.rem(5) == 0L) {
                    Ad(Random.nextLong(), "https://netology.ru", "figma.jpg")
                } else {
                    null
                }
            }
    }


    // override val data = dao.getAll().map(List<PostEntity>::toListDto)

    override suspend fun getAttachmentUrl(fileName: String): String {
        return apiService.getAttachmentUrl(fileName)
    }

    override suspend fun getAvatarUrl(fileName: String): String {
        return apiService.getAvatarUrl(fileName)
    }

//    override fun getNewerCount(id: Long): Flow<Int> = flow {
//        while (true) {
//            delay(10_000L)
//            val response = apiService.getNewer(id)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(body.toEntity())
//            emit(body.size)
//        }
//    }
//        .catch { e -> throw AppError.from(e) }
//        .flowOn(Dispatchers.Default)


    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000L)
                val response = apiService.getNewer(id)
                val posts = response.body().orEmpty()
                emit(posts.size)
                dao.insert(posts.toEntity().map { it.copy(hidden = true) })

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
    override suspend fun saveWithAttachment(post: Post, upload: PhotoModel) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    // override suspend fun upload(upload: MediaUpload): Media {
    override suspend fun upload(upload: PhotoModel): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun showAll() {
        try {
            dao.showAll()
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            val posts = response.body() ?: throw RuntimeException("body is null")
            dao.insert(posts.map { PostEntity.fromDto(it) })
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Long): Post {
        return try {
            val response = apiService.getById(id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            val post = response.body() ?: throw RuntimeException("body is null")
            dao.insert(PostEntity.fromDto(post))
            post
        } catch (e: IOException) {
            dao.getById(id)?.toDto() ?: throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long): Post {
        dao.likeById(id)
        val response = apiService.likeById(id)
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }
        val post = response.body() ?: throw RuntimeException("body is null")
        dao.insert(PostEntity.fromDto(post))
        return post
    }

    override suspend fun unLikeById(id: Long): Post {
        dao.likeById(id)
        val response = apiService.dislikeById(id)
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }
        val post = response.body() ?: throw RuntimeException("body is null")
        dao.insert(PostEntity.fromDto(post))
        return post
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        val response = apiService.removeById(id)
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }

    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw RuntimeException(response.errorBody()?.string())
            }
            dao.insert(
                PostEntity.fromDto(
                    response.body() ?: throw RuntimeException("body is null")
                )
            )

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }


}
