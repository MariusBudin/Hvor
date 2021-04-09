package com.mariusbudin.hvor.core.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

fun ImageView?.load(imageUrl: String?) {
    this?.let {
        Glide.with(this)
            .load(imageUrl)
            .into(this)
    }
}

fun ImageView?.loadCircle(imageUrl: String?) {
    this?.let {
        Glide.with(this)
            .load(imageUrl)
            .transform(CircleCrop())
            .into(this)
    }
}