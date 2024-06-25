package ru.netology.nmedia.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

    fun ImageView.loadAvatar(url: String) {

        Glide.with(this)
            .load("http://10.0.2.2:9999/avatars/${url}")
            .circleCrop()
            .placeholder(R.drawable.ic_loading_100dp)
            .error(R.drawable.ic_error_100dp)
            .timeout(10_000)
            .into(this)
    }
