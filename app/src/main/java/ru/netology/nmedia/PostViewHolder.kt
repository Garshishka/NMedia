package ru.netology.nmedia

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.PostLayoutBinding

class PostViewHolder(
    private val binding: PostLayoutBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root)
{
    private val likeNotPressed = R.drawable.ic_baseline_favorite_border_24
    private val likePressed = R.drawable.ic_baseline_favorite_24

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likesText.text = formattingBigNumbers(post.likesAmount)
            likesIcon.setImageResource(if (post.likedByMe) likePressed else likeNotPressed)
            likesIcon.setOnClickListener { onLikeListener(post) }
            shareText.text = formattingBigNumbers(post.sharesAmount)
            shareIcon.setOnClickListener { onShareListener(post) }
            viewsText.text = formattingBigNumbers(post.views)
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