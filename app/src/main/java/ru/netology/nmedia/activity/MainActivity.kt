package ru.netology.nmedia.activity

import android.os.Bundle

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.CardPostBinding

import ru.netology.nmedia.viewmodel.PostViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter{
            viewModel.likeById(it.id)
        }

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->

            adapter.list = posts
          // binding.container.removeAllViews()

//            posts.map { post ->
//                CardPostBinding.inflate(layoutInflater, binding.container, false).apply {
//                    author.text = post.author
//                    published.text = post.published
//                    content.text = post.content
//
//                    shared.text = countWithSuffix(post.shared)
//                    likeCount.text = countWithSuffix(post.likes)
//                    viewsCount.text = countWithSuffix(post.viewsCount)
//
//                    like.setImageResource(
//                        if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
//                    )
//                    share.setOnClickListener {
//                        viewModel.sharedById(post.id,post.shared+100)
//                    }
//
//                    like.setOnClickListener {
//                        viewModel.likeById(post.id)
//                    }
//                }.root
//            }.forEach{
//                binding.container.addView(it)
//            }
            
        }

    }


}
