package ru.netology.nmedia.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.Post
import ru.netology.nmedia.databinding.PostLayoutBinding

typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit
typealias OnRemoveListener = (post: Post) -> Unit

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}