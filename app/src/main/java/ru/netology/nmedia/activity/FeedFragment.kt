package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.activity.PictureFragment.Companion.urlArg
import ru.netology.nmedia.activity.SinglePostFragment.Companion.idArg
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.viewholder.OnInteractionListener
import ru.netology.nmedia.viewholder.PostsAdapter
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    lateinit var binding: FragmentFeedBinding

    private val interactionListener = object : OnInteractionListener {

        override fun onEdit(post: Post) {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply
                { textArg = post.content })
            viewModel.edit(post)
        }

        override fun onLike(post: Post) {
            val token = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
                ?.getString("TOKEN_KEY", null)
            if (token == null) {
                binding.signInTab.isVisible = true
            } else {
                viewModel.likeById(post.id, post.likedByMe)
            }
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
            findNavController().navigate(
                R.id.action_feedFragment_to_singlePostFragment,
                Bundle().apply
                { idArg = post.id })
        }
    }

    val adapter = PostsAdapter(interactionListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        binding.list.adapter = adapter
        subscribe()

        return binding.root
    }


    private fun subscribe() {

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiper.isRefreshing =
                    it.refresh is LoadState.Loading
                            || it.append is LoadState.Loading
                            || it.prepend is LoadState.Loading
            }
        }

        authViewModel.state.observe(viewLifecycleOwner) {
            if (it?.id != -1L) {
                adapter.refresh()
            }
        }

        /* viewModel.newerCount.observe(viewLifecycleOwner) {
             if (it > 0) {
                 binding.newerPostsButton.isVisible = true
                 binding.newerPostsButton.text = getString(R.string.newer_posts, it.toString())
             } else {
                 binding.newerPostsButton.isVisible = false
             }
             println("Newer count $it")
         }*/

        viewModel.postCreatedError.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                getString(R.string.specific_posting_error, it),
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry post") {
                    viewModel.load()
                }
                .show()
        }

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

        binding.swiper.setOnRefreshListener {
            adapter.refresh()
        }

        binding.retry.setOnClickListener {
            viewModel.load()
        }

        binding.newerPostsButton.setOnClickListener {
            binding.newerPostsButton.isVisible = false
            viewModel.showNewPosts()
        }

        binding.fab.setOnClickListener {
            val token = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
                ?.getString("TOKEN_KEY", null)
            if (token == null) {
                binding.signInTab.isVisible = true
            } else {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
        }

        binding.signInButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
        }

        binding.goBackButton.setOnClickListener {
            binding.signInTab.isVisible = false
        }
    }
}
