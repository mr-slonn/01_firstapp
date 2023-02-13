package ru.netology.nmedia

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
             likes = 999
        )

        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            shared.text = countWithSuffix(post.shared)
            viewsCount.text = countWithSuffix(post.viewsCount)
            if (post.likedByMe) {
                like.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
            likeCount.text = post.likes.toString()

            root.setOnClickListener {
                Log.d("stuff", "stuff")
            }

            avatar.setOnClickListener {
                Log.d("stuff", "avatar")
            }

            share.setOnClickListener {
                post.shared += 100
                shared.text = countWithSuffix(post.shared)
            }

            like.setOnClickListener {
                Log.d("stuff", "like")
                post.likedByMe = !post.likedByMe
                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
                if (post.likedByMe) post.likes++ else post.likes--
                likeCount.text =  countWithSuffix (post.likes)
            }
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
