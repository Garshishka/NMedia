package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.*
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewholder.OnInteractionListener
import ru.netology.nmedia.viewholder.PostsAdapter


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel: PostViewModel by viewModels()

    val newPostLauncher = registerForActivityResult(NewOrEditPostResultContract()) { result ->
        result ?: return@registerForActivityResult
        viewModel.changeContent(result)
        viewModel.save()
    }

    private val interactionListener = object : OnInteractionListener {

        override fun onEdit(post: Post) {
            newPostLauncher.launch(post.content)
            viewModel.edit(post)
        }

        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)

            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onVideoClick(post: Post) {
            val intent =  Intent(Intent.ACTION_VIEW, Uri.parse(post.attachedVideo))
            startActivity(intent)
        }
    }


    val adapter = PostsAdapter(interactionListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.adapter = adapter
        subscribe()
        //setupListeners()
    }

    private fun subscribe() {
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener{
            newPostLauncher.launch("")
        }

        /*viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                with(binding.content) {
                    requestFocus()
                    setText(post.content)
                }
            }
        }*/
    }

    /*private fun setupListeners() {


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

    }*/

    /*private fun descriptorVisibility(willShow: Boolean) {
        binding.contentDescriptorGroup.visibility = if (willShow) View.VISIBLE else View.GONE
    }*/
}
