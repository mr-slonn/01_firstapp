package ru.netology.nmedia.dto

data class Post(
    override val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shared: Int = 0,
    var viewsCount: Int = 0,
    var video: String? = null,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
) : FeedItem()
