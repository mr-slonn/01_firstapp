package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var shared: Int = 0,
    var viewsCount: Int = 0,
    var video: String? = null,
    @Embedded val attachment: AttachmentEntity? = null,
    val hidden: Boolean = false
) {
    fun toDto() = Post(
        id,
        author,
        authorId,
        authorAvatar,
        content,
        published,
        likedByMe,
        likes,
        shared,
        viewsCount,
        video,
        attachment?.toDto(),
        ownedByMe = false
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorId,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.shared,
                dto.viewsCount,
                dto.video,
                AttachmentEntity.fromDto(dto.attachment),
                hidden = false,
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
