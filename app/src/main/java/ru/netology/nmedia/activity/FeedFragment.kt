package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(false)

        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                //  findNavController().navigate(R.id.action_feedFragment_to_newPostFragment, Bundle().apply { textArg = post.content })
            }

            override fun onLike(post: Post) {
//                if (post.likedByMe) {
//                    viewModel.unLikeById(post.id)
//                } else {
//                    viewModel.likeById(post.id)
//                }

                viewModel.likeByIdV2(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.sharedById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onPlayVideo(post: Post) {
                if (!post.video.isNullOrBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    startActivity(intent)
                }
            }

            override fun onViewPost(post: Post) {

                findNavController().navigate(
                    R.id.action_feedFragment_to_PostFragment,
                    Bundle().apply { textArg = post.id.toString() })
            }
        })
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.progress.isVisible = false
                binding.swipeRefreshLayout.isRefreshing = state.loading
            } else {
                binding.progress.isVisible = state.loading

            }
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty

            if (state.smallError) {
                Snackbar.make(
                    binding.root,
                    R.string.error_loading,
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                ).setAction(android.R.string.cancel)
                {

                }.show()
            }

        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }


        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply { textArg = post.content })
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPosts()

        }

        return binding.root
    }
}
