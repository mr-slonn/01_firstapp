package ru.netology.nmedia.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import ru.netology.nmedia.R

import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.services.Services
import ru.netology.nmedia.util.loadAvatar
import ru.netology.nmedia.util.loadMediaAttachment


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onPlayVideo(post: Post) {}
    fun onViewPost(post: Post) {}
    fun onViewAttachment(post: Post) {}


}


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val authorized:Boolean
) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, authorized)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val authorized:Boolean

) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = Services().getDateTime(post.published)
            content.text = post.content

            share.text = Services().countWithSuffix(post.shared)
            like.text = Services().countWithSuffix(post.likes)
            like.isChecked = post.likedByMe
            viewsCount.text = Services().countWithSuffix(post.viewsCount)


            if (post.authorAvatar != null) {
                avatar.loadAvatar(post.authorAvatar)
            }

            if (post.attachment != null) {
                //attachment.loadAttachment(post.attachment.url)
                attachment.loadMediaAttachment(post.attachment.url)
                attachment.visibility = View.VISIBLE
            } else {
                attachment.visibility = View.GONE
            }



            if (!post.video.isNullOrBlank()) {
                videoLayout.visibility = View.VISIBLE
                content.visibility = View.GONE
            } else {
                videoLayout.visibility = View.GONE
                content.visibility = View.VISIBLE
            }

            videoLayout.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }
            videoButton.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }

            content.setOnClickListener {
                onInteractionListener.onViewPost(post)
            }


//            like.setImageResource(
//                if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
//            )

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            like.isCheckable = authorized


            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            attachment.setOnClickListener {
                onInteractionListener.onViewAttachment(post)
            }
            menu.isVisible = post.ownedByMe
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

