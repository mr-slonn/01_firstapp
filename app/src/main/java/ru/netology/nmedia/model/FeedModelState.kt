package ru.netology.nmedia.model

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val smallError: Boolean = false,
    val refreshing: Boolean = false,
    val onePost: Long = 0,
)
