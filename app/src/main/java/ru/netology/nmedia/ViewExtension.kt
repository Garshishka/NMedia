package ru.netology.nmedia

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.load(url: String, willCrop: Boolean = false, timeout: Int = 10_000) {
    Glide.with(this)
        .load(url)
        .apply { if (willCrop) circleCrop() }
        .error(R.drawable.ic_baseline_error_outline_48)
        .placeholder(R.drawable.ic_baseline_downloading_48)
        .timeout(timeout)
        .into(this)
}
