package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    companion object {
        var Bundle.urlArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onEdit(post: Post) {
                viewModel.edit(post)

            }

            override fun photoView(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_PhotoView,
                    Bundle().apply { urlArg = post.attachment?.url })
            }


            override fun onLike(post: Post) {
                if (!post.likedByMe) viewModel.likeById(post.id) else viewModel.unLikeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override suspend fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                viewModel.sharedById(post.id)
                val shareIntent = Intent.createChooser(intent, "Share Post")
                startActivity(shareIntent)
            }

            override fun onPlay(post: Post) {
                val videoUri = viewModel.getVideoUri(post)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    type = "video/*"
                    data = videoUri
                }
                startActivity(Intent.createChooser(intent, "video"))
            }

            override fun onImageClick(imageUrl: String) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_PhotoView,
                    Bundle().apply {
                        putString("imageUrl", imageUrl)
                    }
                )
            }

        })
        { imageUrl ->
            onImageClick(imageUrl)
        }
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty

            viewModel.newerCount.observe(viewLifecycleOwner) { state ->

                if (state >= 1) binding.newPostReload.visibility = View.VISIBLE

                println("Newer: $state")
            }

            binding.newPostReload.setOnClickListener {
                it.visibility = View.GONE
                viewModel.showNewPosts()
                binding.list.smoothScrollToPosition(0)
            }


            viewModel.state.observe(viewLifecycleOwner) { state ->
                binding.progress.isVisible = state.loading
                if (state.error) {
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.retry_loading) {
                            viewModel.loadPosts()
                        }
                        .show()
                }
                binding.refresh.isRefreshing = state.refreshing

            }
            binding.refresh.setOnRefreshListener {
                viewModel.loadPosts()
            }

            binding.addNewPost.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }

            viewModel.edited.observe(viewLifecycleOwner) { post ->
                if (post.id == 0L) {
                    return@observe
                }
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }
        }

        return binding.root
    }

    private fun onImageClick(url: String) {
        findNavController().navigate(
            R.id.action_feedFragment_to_PhotoView,
            Bundle().apply {
                putString("imageUrl", url)
            }
        )
    }
}




