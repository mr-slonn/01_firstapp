package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels

import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.mediaArg
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
//import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
//import ru.netology.nmedia.viewmodel.ViewModelFactory
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {
    //private val dependencyContainer = DependencyContainer.getInstance()

//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment,
//        factoryProducer = {
//            ViewModelFactory(dependencyContainer.repository,dependencyContainer.appAuth, dependencyContainer.authRepository)
//        }
//    )
//
//    private val authViewModel: AuthViewModel by viewModels(
//        ownerProducer = ::requireParentFragment,
//        factoryProducer = {
//            ViewModelFactory(dependencyContainer.repository,dependencyContainer.appAuth, dependencyContainer.authRepository)
//        }
//    )

//    private val viewModel: PostViewModel by viewModels(
//       ownerProducer = ::requireParentFragment)
//    private val authViewModel: AuthViewModel by viewModels(
//        ownerProducer = ::requireParentFragment)

    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    @Inject
    lateinit var appAuth: AppAuth

//    private val viewModel: PostViewModel by activityViewModels()


    fun showDialog(signIn: Boolean) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle(R.string.account)
        //set message for alert dialog

        builder.setMessage(if (signIn) R.string.confirm_log_in else R.string.access_log_out)
        builder.setIcon(R.drawable.baseline_login_24)

        if (signIn)
        //performing positive action
        {
            builder.setPositiveButton(R.string.sign_auth) { _, _ ->
                findNavController().navigate(
                    R.id.action_feedFragment_to_logInFragment,
                    Bundle().apply {
                        textArg = "signin"
                    })
            }

            builder.setNegativeButton(R.string.sign_up) { _, _ ->
                findNavController().navigate(
                    R.id.action_feedFragment_to_logInFragment,
                    Bundle().apply {
                        textArg = "signup"
                    })
            }
        } else {
            builder.setPositiveButton(R.string.sign_out) { _, _ ->
                appAuth.removeAuth()
            }
        }

        //performing cancel action
        builder.setNeutralButton(R.string.description_post_cancel) { _, _ ->

        }
        //performing negative action

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        var authorized = false

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(false)

        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(
            object : OnInteractionListener {
                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onLike(post: Post) {

                    if (!authorized) {
                        showDialog(true)
                    } else {
                        viewModel.likeByIdV2(post)
                    }
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onShare(post: Post) {
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
                        Bundle().apply {
                            textArg = post.id.toString()

                        })
                }

                override fun onViewAttachment(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_photoFragment,
                        Bundle().apply {
                            mediaArg = post.attachment?.url
                        })
                }
            }, authorized
        )
        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            //binding.errorGroup.isVisible = state.error

            if (state.smallError || state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModel.loadPosts()
                    }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }


        binding.fab.setOnClickListener {

            if (!authorized) {
                showDialog(true)
            } else {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }

        }


        viewModel.edited.observe(viewLifecycleOwner) { editedPost ->
            if (editedPost.id != 0L) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = editedPost.content
                        mediaArg = editedPost.attachment?.url
                    })
            }
        }

        viewModel.newerCount.observe(viewLifecycleOwner) { state ->
            Log.d("FeedFragment", "New count: $state")
            if (state != 0 && binding.retryButton.visibility == View.GONE) {
                binding.reloadNewPosts.isVisible = true
            }
        }


        binding.reloadNewPosts.setOnClickListener {
            viewModel.showHiddenPosts()
            binding.list.smoothScrollToPosition(0)
            it.isVisible = false
        }

//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                if (positionStart == 0) {
//                    binding.list.smoothScrollToPosition(0)
//                }
//            }
//        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPosts()

        }

//        val authViewModel: AuthViewModel by viewModels()
        var currentMenuProvider: MenuProvider? = null


        authViewModel.data.observe(viewLifecycleOwner) { token ->
            authorized = token.token != null

            currentMenuProvider?.let {
                requireActivity().removeMenuProvider(it)
            }
            requireActivity().addMenuProvider(
                object : MenuProvider {
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.menu_main, menu)
                        menu.setGroupVisible(R.id.unauthenticated, !authorized)
                        menu.setGroupVisible(R.id.authenticated, authorized)
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                        when (menuItem.itemId) {
                            R.id.signin -> {
                                //AppAuth.getInstance().setAuth(5, "x-token")
                                findNavController().navigate(
                                    R.id.action_feedFragment_to_logInFragment,
                                    Bundle().apply {
                                        textArg = "signin"
                                    })
                                true
                            }

                            R.id.signup -> {

                                //AppAuth.getInstance().setAuth(5, "x-token")
                                findNavController().navigate(
                                    R.id.action_feedFragment_to_logInFragment,
                                    Bundle().apply {
                                        textArg = "signup"
                                    })
                                true
                            }

                            R.id.signout -> {
                                showDialog(false)
                                //AppAuth.getInstance().removeAuth()
                                true
                            }

                            else ->
                                false

                            ///super.onOptionsItemSelected(item)

                        }
                }.also {
                    currentMenuProvider = it
                },
                viewLifecycleOwner,
            )
        }

        return binding.root
    }
}
