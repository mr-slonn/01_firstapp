package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter(object : OnInteractionListener {


            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShared(post: Post) {
                viewModel.sharedById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent,"Share post")
                startActivity(shareIntent)
            }



            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }
        }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val isNewPost = adapter.currentList.size < posts.size

            adapter.submitList(posts)
            {
                if (isNewPost) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                binding.editedContent.text = post.content
                binding.group.visibility = View.VISIBLE
            }
            with(binding.content) {
                //requestFocus()
                focusAndShowKeyboard()
                setText(post.content)
            }
        }

        binding.cancel.setOnClickListener {

            with(binding.content) {

                binding.group.visibility = View.GONE

                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)

            }
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")
                clearFocus()
                binding.group.visibility = View.GONE
                AndroidUtils.hideKeyboard(this)
            }
        }
    }


}
