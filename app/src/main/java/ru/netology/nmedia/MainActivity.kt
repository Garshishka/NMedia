package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val likeNotPressed = R.drawable.ic_baseline_favorite_border_24
    val likePressed = R.drawable.ic_baseline_favorite_24


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:56",
            likesAmount = 999,
            sharesAmount = 9_995,
            likedByMe = false,
            views = 12_232_342
        )

        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likesText.text = formattingBingNumbers(post.likesAmount)
            shareText.text = formattingBingNumbers(post.sharesAmount)
            likesIcon.setImageResource(if (post.likedByMe) likePressed else likeNotPressed)
            viewsText.text = formattingBingNumbers(post.views)

            likesIcon?.setOnClickListener {
                post.likedByMe = !post.likedByMe
                post.likesAmount = if (post.likedByMe) post.likesAmount+1 else post.likesAmount-1
                likesText.text = formattingBingNumbers( post.likesAmount)
                likesIcon.setImageResource(if (post.likedByMe) likePressed else likeNotPressed)
            }

            shareIcon?.setOnClickListener {
                post.sharesAmount =post.sharesAmount +1
                shareText.text = formattingBingNumbers(post.sharesAmount)
            }
        }
    }

    fun formattingBingNumbers(number: Long) : String{
        when(number){
            in 0..999 -> return number.toString()
            in 1000..1099 -> return "1k"
            in 1100..9_999 -> return (number.toDouble()/1000).toString().take(3)+"K"
            in 10_000..99_999 -> return (number.toDouble()/1000).toString().take(2)+"K"
            in 100_000..999_999 -> return (number.toDouble()/1000).toString().take(3)+"K"
            else ->{
                val mNumber = (number.toDouble()/1_000_000).toString()
                val strings = mNumber.split(".")
                return strings[0]+"."+strings[1].take(1)+"M"
            }
        }
    }
}
