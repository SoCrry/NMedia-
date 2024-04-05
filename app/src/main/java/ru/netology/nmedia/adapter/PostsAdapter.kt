package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.Post
import ru.netology.nmedia.service.NumberServices

typealias OnLikeListener = (post: Post) -> Unit
typealias OnSharedListener = (post: Post) -> Unit

class PostsAdapter(
    private val onLikeListener: OnLikeListener,
    private val onSharedListener: OnSharedListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {


    var list = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onSharedListener)
    }


    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        holder.bind(post)
    }
}


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onSharedListener: OnSharedListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            timePublication.text = post.published
            content.text = post.content

            like.text = NumberServices().countWithSuffix(post.like)
            shared.text = NumberServices().countWithSuffix(post.shared)
            viewers.text = NumberServices().countWithSuffix(post.views)

            likesIv.setImageResource(
                if (post.likeByMe) R.drawable.liked else R.drawable.likes
            )
            likesIv.setOnClickListener {
                onLikeListener(post)
            }
            sharedIv.setOnClickListener {
                onSharedListener(post)
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