package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter(
            object : OnInteractionListener {


                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onLike(post: Post) {
                    viewModel.likeByIdV2(post)
                }

                override fun onShare(post: Post) {

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, "Share post")
                    startActivity(shareIntent)
                }


                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onPlayVideo(post: Post) {
                    if (!post.video.isNullOrBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                        startActivity(intent)
                    }
                }
            }, false
        )

        binding.list.adapter = adapter


//        viewModel.data.observe(this) { state ->
//            val isNewPost = adapter.currentList.size < state.posts.size
//            adapter.submitList(state.posts)
//            {
//                if (isNewPost) {
//                    binding.list.smoothScrollToPosition(0)
//                }
//            }
//        }


        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }



        binding.fab.setOnClickListener {
            newPostLauncher.launch(null)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                newPostLauncher.launch(post.content)
            }

        }
    }
}
