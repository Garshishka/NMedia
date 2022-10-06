package ru.netology.nmedia.viewholder

import ru.netology.nmedia.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onVideoClick(post: Post){}
}