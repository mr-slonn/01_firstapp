package ru.netology.nmedia

import android.os.Bundle

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding

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

        val viewModel: PostViewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                shared.text = countWithSuffix(post.shared)
                viewsCount.text = countWithSuffix(post.viewsCount)
                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
                likeCount.text = post.likes.toString()
            }
        }

        binding.share.setOnClickListener {
            viewModel.shared((viewModel.data.value?.shared ?: 0) + 100)
        }

        binding.like.setOnClickListener {
            viewModel.like()
        }

    }

    private fun countWithSuffix(count: Int): String {
        val value = count.toDouble()
        val suffixChars = "KMGTPE"
        val formatter = DecimalFormat("##.#")
        formatter.roundingMode = RoundingMode.DOWN
        return if (value < 1000.0) formatter.format(value)
        else {
            val exp = (ln(value) / ln(1000.0)).toInt()
            val preFormat = value / 1000.0.pow(exp.toDouble())
            if (preFormat >= 10.0 || preFormat == 1.0) {
                preFormat.toInt().toString() + suffixChars[exp - 1]
            } else {
                formatter.format(preFormat) + suffixChars[exp - 1]
            }
        }
    }
}
