package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewById<ImageButton>(R.id.likesIcon).setOnClickListener{
            if(it !is ImageButton){
                return@setOnClickListener
            }
            it.setImageResource(R.drawable.ic_baseline_favorite_24)
        }
    }
}