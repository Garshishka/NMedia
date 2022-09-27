package ru.netology.nmedia

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewholder.OnInteractionListener
import ru.netology.nmedia.viewholder.PostsAdapter


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel: PostViewModel by viewModels()
    val interactionListener = object : OnInteractionListener {

        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }
    }

    val adapter = PostsAdapter(interactionListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.adapter = adapter
        subscribe()
        setupListeners()
    }

    private fun subscribe() {
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                with(binding.content) {
                    requestFocus()
                    setText(post.content)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.content.setOnFocusChangeListener { view, b ->
            if(b) descriptorVisibility(true)
        }
        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Content can't be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.changeContent(text.toString())
                    viewModel.save()
                    setText("")
                    clearFocus()
                    descriptorVisibility(false)
                    AndroidUtils.hideKeyboard(this)
                }
            }
        }
        binding.closeEdit.setOnClickListener {
            with(binding.content) {
                setText("")
                clearFocus()
                descriptorVisibility(false)
                AndroidUtils.hideKeyboard(this)
            }
            viewModel.empty()
        }
    }

    private fun descriptorVisibility(willShow: Boolean) {
        binding.contentDescriptorGroup.visibility = if (willShow) View.VISIBLE else View.GONE
    }
}
