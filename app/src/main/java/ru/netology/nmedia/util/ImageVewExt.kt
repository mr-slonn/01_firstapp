package ru.netology.nmedia.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

fun ImageView.loadAvatar(url: String) {

    Glide.with(this)
        .load("http://10.0.2.2:9999/avatars/${url}")
        .circleCrop()
        .placeholder(R.drawable.ic_netology_48dp)
        .error(R.drawable.baseline_warning_amber_24)
        .timeout(10_000)
        .into(this)
}

fun ImageView.loadAttachment(url: String) {

    Glide.with(this)
        .load("http://10.0.2.2:9999/images/${url}")
        .fallback(null)
        .timeout(10_000)
        .into(this)
}

fun ImageView.loadMediaAttachment(url: String) {

    Glide.with(this)
        .load("http://10.0.2.2:9999/media/${url}")
        .placeholder(R.drawable.baseline_sync_24)
        .error(R.drawable.baseline_warning_amber_24)
        .fallback(null)
        .timeout(10_000)
        .into(this)


}


