package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.Post
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.utils.LongArg
import ru.netology.nmedia.viewholder.OnInteractionListener
import ru.netology.nmedia.viewholder.PostViewHolder

class SinglePostFragment : Fragment() {
    val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

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

        viewModel.data.observe(viewLifecycleOwner) { feedModel ->
            val post = feedModel.posts.find { it.id == findId } ?: run {
                findNavController().navigateUp()
                return@observe
            }
            viewHolder.bind(post)
        }

        viewModel.postsEditError.observe(viewLifecycleOwner) {
            Toast.makeText(
                activity,
                getString(R.string.specific_edit_error, it),
                Toast.LENGTH_LONG
            )
                .show()
        }

        return binding.root
    }

    companion object {
        var Bundle.idArg by LongArg
    }
}