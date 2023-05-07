package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class PostModel(
    val post: Post? = null,
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
)
