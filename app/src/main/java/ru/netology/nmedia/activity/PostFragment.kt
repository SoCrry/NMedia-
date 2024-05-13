package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {
    companion object {
        var Bundle.idArg: Long by LongArg
    }

    private val postViewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )

        val postId = arguments?.idArg


        postViewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.firstOrNull { it.id == postId } ?: return@observe
            val viewHolder = PostViewHolder(binding, object : OnInteractionListener {

                override fun onEdit(post: Post) {
                    postViewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_postFragment_to_newPostFragment,
                        Bundle().also { it.textArg = post.content }
                    )
                }

                override fun onLike(post: Post) {
                    postViewModel.likeById(post.id)
                }

                override fun onRemove(post: Post) {
                    postViewModel.removeById(post.id)
                    findNavController().navigateUp()
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.content)
                    }

                    val shareIntent = Intent.createChooser(intent, "Share Post")
                    startActivity(shareIntent)
                }

                override fun onPlay(post: Post) {
                    val videoUri = postViewModel.getVideoUri(post)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        type = "video/*"
                        data = videoUri
                    }
                    startActivity(Intent.createChooser(intent, "video"))
                }

                override fun onCardPost(post: Post) {
                }

            })
            viewHolder.bind(post)
        }
        return binding.root
    }

}