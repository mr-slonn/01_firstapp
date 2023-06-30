package ru.netology.nmedia.repository

import androidx.lifecycle.*
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import okio.IOException
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.lang.RuntimeException


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll().map {
        it.map(PostEntity::toDto)
    }

    // override val data = dao.getAll().map(List<PostEntity>::toListDto)

    override suspend fun getAttachmentUrl(fileName: String): String {
        return PostsApi.retrofitService.getAttachmentUrl(fileName)
    }

    override suspend fun getAvatarUrl(fileName: String): String {
        return PostsApi.retrofitService.getAvatarUrl(fileName)
    }


    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll()
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
            val response = PostsApi.retrofitService.getById(id)
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
        val response = PostsApi.retrofitService.likeById(id)
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }
        val post = response.body() ?: throw RuntimeException("body is null")
        dao.insert(PostEntity.fromDto(post))
        return post
    }

    override suspend fun unLikeById(id: Long): Post {
        dao.likeById(id)
        val response = PostsApi.retrofitService.dislikeById(id)
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }
        val post = response.body() ?: throw RuntimeException("body is null")
        dao.insert(PostEntity.fromDto(post))
        return post
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        val response = PostsApi.retrofitService.removeById(id)
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }

    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.retrofitService.save(post)
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
