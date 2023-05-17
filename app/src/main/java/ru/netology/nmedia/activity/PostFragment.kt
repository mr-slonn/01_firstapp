package ru.netology.nmedia.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
//import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.services.Services
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class PostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var _binding: FragmentPostBinding? = null

    private fun onPlayVideo(post: Post) {
        if (!post.video.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
            startActivity(intent)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        requireActivity().onBackPressedDispatcher.addCallback {
            actionBar?.setDisplayHomeAsUpEnabled(false)
            actionBar?.setDisplayShowHomeEnabled(false)
            actionBar?.title = getString(R.string.nmedia)
            binding.cardPostScroll.visibility = View.GONE
            viewModel.loadPosts()
            viewModel.backPost()
            findNavController().navigateUp()
        }
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    private val binding: FragmentPostBinding
        get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val post = arguments?.textArg?.toLong()?.let { viewModel.getPost(it) }
//            ?: throw NullPointerException("Oops! Post is not found")


        // val post = arguments?.textArg?.toLong()?.let { viewModel.getPost(it) }
        arguments?.textArg?.toLong()?.let { viewModel.getPost(it) }

        viewModel.post.observe(viewLifecycleOwner) { state ->

            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            if (state.post != null) {

                binding.cardPostScroll.visibility = View.VISIBLE

                binding.cardPost.apply {
                    author.text = state.post.author
                    published.text = state.post.published
                    content.text = state.post.content
                    like.isChecked = state.post.likedByMe
                    like.text = Services().countWithSuffix(state.post.likes)
                    share.text = Services().countWithSuffix(state.post.shared)
                    viewsCount.text = Services().countWithSuffix(state.post.viewsCount)

                    if (!state.post.video.isNullOrBlank()) {
                        videoLayout.visibility = View.VISIBLE
                        content.visibility = View.GONE
                    } else {
                        videoLayout.visibility = View.GONE
                        content.visibility = View.VISIBLE
                    }

                    videoLayout.setOnClickListener {
                        onPlayVideo(state.post)
                    }
                    videoButton.setOnClickListener {
                        onPlayVideo(state.post)
                    }

                    share.setOnClickListener {
                        viewModel.sharedById(state.post.id)
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, state.post.content)
                            type = "text/plain"
                        }

                        val shareIntent =
                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                    }

                    like.setOnClickListener {
//                        if (state.post.likedByMe) {
//                            viewModel.unLikeByIdFromPost(state.post.id)
//                        } else {
//                            viewModel.likeByIdFromPost(state.post.id)
//                        }

                        viewModel.likeByIdFromPostV2()

                    }

                    menu.setOnClickListener {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.options_post)
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.remove -> {
                                        viewModel.post.removeObservers(viewLifecycleOwner)
                                        viewModel.removeById(state.post.id)
                                        //findNavController().navigateUp()
                                        findNavController().navigate(
                                            R.id.action_PostFragment_to_feedFragment
                                        )
                                        true
                                    }

                                    R.id.edit -> {
                                        viewModel.edit(state.post)
                                        findNavController().navigate(
                                            R.id.action_PostFragment_to_newPostFragment2,
                                            Bundle().apply { textArg = state.post.content })
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

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
