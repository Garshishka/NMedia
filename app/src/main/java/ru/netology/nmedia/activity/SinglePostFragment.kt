package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.activity.PictureFragment.Companion.urlArg
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.utils.LongArg
import ru.netology.nmedia.viewholder.OnInteractionListener
import ru.netology.nmedia.viewholder.PostViewHolder
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class SinglePostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    private val interactionListener = object : OnInteractionListener {

        override fun onEdit(post: Post) {
            findNavController().navigate(
                R.id.action_singlePostFragment_to_newPostFragment,
                Bundle().apply
                { textArg = post.content })
            viewModel.edit(post)
        }

        override fun onLike(post: Post) {
            viewModel.likeById(post.id, post.likedByMe)
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
            findNavController().navigateUp()
        }

        override fun onPictureClick(url: String) {
            findNavController().navigate(R.id.action_feedFragment_to_pictureFragment,
                Bundle().apply
                { urlArg = url })
        }

        override fun onVideoClick(post: Post) {
            // val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.attachment.url))
            //startActivity(intent)
            // TODO: Make video click working again
        }

        override fun onPostClick(post: Post) {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSinglePostBinding.inflate(inflater, container, false)

        val viewHolder = PostViewHolder(binding.singlePost, interactionListener)

        val findId = arguments?.idArg

        val adapter = PostsAdapter(interactionListener)


        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                val post = adapter.snapshot().items.find { it.id == findId } ?: run {
                    findNavController().navigateUp()
                    return@collectLatest
                }
                viewHolder.bind(post as Post)
            }
        }

        /*viewModel.data.observe(viewLifecycleOwner) { feedModel ->
            val post = feedModel.posts.find { it.id == findId } ?: run {
                findNavController().navigateUp()
                return@observe
            }
            viewHolder.bind(post)
        }*/

        viewModel.postsRemoveError.observe(viewLifecycleOwner) {
            val id = it.second
            Snackbar.make(
                binding.root,
                getString(R.string.specific_edit_error, it.first),
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry") {
                    viewModel.removeById(id)
                }
                .show()
        }

        viewModel.postsLikeError.observe(viewLifecycleOwner) {
            val id = it.second.first
            val willLike = it.second.second
            Snackbar.make(
                binding.root,
                getString(R.string.specific_edit_error, it.first),
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry") {
                    viewModel.likeById(id, willLike)
                }
                .show()
        }

        return binding.root
    }

    companion object {
        var Bundle.idArg by LongArg
    }
}