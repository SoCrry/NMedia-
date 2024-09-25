package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.service.NumberServices
import ru.netology.nmedia.util.loadAvatar
import ru.netology.nmedia.viewExtensions.load


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    suspend fun onShare(post: Post) {}
    fun onPlay(post: Post) {}
    fun onCardPost(post: Post) {}
    fun photoView(post: Post) {}
    fun onImageClick(imageUrl: String)


}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val onImageClick: (String) -> Unit
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener,onImageClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val onImageClick: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            timePublication.text = post.published
            content.text = post.content
            if (!post.urlVideo.isNullOrBlank()) {
                videoGroup.visibility = View.VISIBLE
                content.visibility = View.VISIBLE
            } else {
                videoGroup.visibility = View.GONE
                content.visibility = View.VISIBLE
            }
            like.text = NumberServices().countWithSuffix(post.likes)
            share.text = NumberServices().countWithSuffix(post.shared)
            viewers.text = NumberServices().countWithSuffix(post.views)
            like.isChecked = post.likedByMe

            menu.isVisible = post.ownedByMe

            if (post.authorAvatar != null) {
                avatar.loadAvatar(post.authorAvatar)
            }
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                /*  onInteractionListener.onShare(post)*/
            }

            playVideo.setOnClickListener {
                onInteractionListener.onPlay(post)
            }
            content.setOnClickListener {
                onInteractionListener.onCardPost(post)
            }
            if (post.attachment != null && post.attachment.type == AttachmentType.IMAGE) {
                binding.postPhoto.visibility = View.VISIBLE
                Glide.with(binding.root)
                    .load(post.attachment.url)
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .timeout(30_000)
                    .into(binding.postPhoto)

                binding.postPhoto.setOnClickListener {
                    onInteractionListener.onImageClick(post.attachment.url)
                }
            } else {
                binding.postPhoto.visibility = View.GONE
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}