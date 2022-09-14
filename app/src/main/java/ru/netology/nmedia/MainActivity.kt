package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val likeNotPressed = R.drawable.ic_baseline_favorite_border_24
    private val likePressed = R.drawable.ic_baseline_favorite_24

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likesText.text = formattingBigNumbers(post.likesAmount)
                likesIcon.setImageResource(if (post.likedByMe) likePressed else likeNotPressed)
                shareText.text = formattingBigNumbers(post.sharesAmount)
                viewsText.text = formattingBigNumbers(post.views)
            }
        }
        binding.likesIcon.setOnClickListener { viewModel.like()  }
        binding.shareIcon.setOnClickListener { viewModel.share() }

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
