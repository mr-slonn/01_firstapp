package ru.netology.nmedia.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter

import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.RecyclerView


import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding

import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
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
    private val authorized: Boolean
) :
    PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    //ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when
                (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("Unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
//        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return PostViewHolder(binding, onInteractionListener, authorized)
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener, authorized)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(
                    binding,
                    //onInteractionListener
                )
            }

            else -> error("Unknown item type $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val post = getItem(position) ?: return
//        holder.bind(post)
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("Unknown item type")
        }


    }

}

class AdViewHolder(
    private val binding: CardAdBinding,
    //  private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad) {
        binding.apply {
            //image.loadMediaAttachment(ad.image)
            image.loadMediaAttachment(ad.image)
//            image.setOnClickListener {
//                onInteractionListener.onAdClick(ad)
//            }
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val authorized: Boolean

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

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}

