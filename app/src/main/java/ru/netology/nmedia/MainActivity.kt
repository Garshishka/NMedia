package ru.netology.nmedia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(
            { viewModel.likeById(it.id) },
            { viewModel.shareById(it.id) },
            {viewModel.removeById(it.id)}
        )
        binding.list.adapter = adapter
        binding.save.setOnClickListener {
            with(binding.content){
                if(text.isNullOrBlank()){
                    Toast.makeText(
                    this@MainActivity,
                    "Content can't be empty",
                    Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
        }
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }
}
