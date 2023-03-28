package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDaoRoom
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryRoomImpl(
    private val dao: PostDaoRoom,
) : PostRepository {
    override fun getAll() = dao.getAll().map { list ->
        list.map {
            it.toDto()
        }
    }

    override fun getById(id: Long): Post? {
        //TODO: не понимаю как вернуть NULL
//       val post =  dao.getById(id)
//        return if (post != null) post.toDto()
//        else null
        return  dao.getById(id).toDto()
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun sharedById(id: Long) {
        dao.sharedById(id)
    }


    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}
