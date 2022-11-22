package ru.netology.nmedia.viewholder

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostLayoutBinding


class PostViewHolder(
    private val binding: PostLayoutBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            wholePost.setOnClickListener { onInteractionListener.onPostClick(post) }
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.text = formattingBigNumbers(post.likes)
            like.isChecked = post.likedByMe
            like.setOnClickListener { onInteractionListener.onLike(post) }
            share.text = formattingBigNumbers(post.sharesAmount)
            share.setOnClickListener { onInteractionListener.onShare(post) }
            viewsText.text = formattingBigNumbers(post.views)

            if (!post.attachedVideo.isNullOrEmpty()) {
                videoGroup.visibility = View.VISIBLE
                videoPicture.setOnClickListener { onInteractionListener.onVideoClick(post) }
            }


            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }

    private fun formattingBigNumbers(number: Long): String {
        return when (number) {
            in 0..999 -> number.toString()
            in 1000..1099 -> "1k"
            in 1100..9_999 -> (number.toDouble() / 1000).toString().take(3) + "K"
            in 10_000..99_999 -> (number.toDouble() / 1000).toString().take(2) + "K"
            in 100_000..999_999 -> (number.toDouble() / 1000).toString().take(3) + "K"
            else -> {
                val mNumber = (number.toDouble() / 1_000_000).toString()
                val strings = mNumber.split(".")
                strings[0] + "." + strings[1].take(1) + "M"
            }
        }
    }
}