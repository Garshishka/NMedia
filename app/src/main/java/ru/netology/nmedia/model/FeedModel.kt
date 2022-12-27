package ru.netology.nmedia.model

import ru.netology.nmedia.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
    val errorText: String = "Some error happened",
)

sealed interface FeedModelState {
    object Idle : FeedModelState
    object Error : FeedModelState
    object Refreshing : FeedModelState
    object Loading : FeedModelState
}