package com.mariusbudin.hvor.presentation.venues.model

import com.mariusbudin.hvor.data.venues.model.Visibility
import com.mariusbudin.hvor.domain.venues.model.PhotoModel
import com.mariusbudin.hvor.presentation.venues.model.Photo.Companion.IMAGE_LARGE_SIZE
import com.mariusbudin.hvor.presentation.venues.model.Photo.Companion.IMAGE_THUMBNAIL_SIZE

data class Photo(
    val thumbnailUrl: String,
    val url: String,
    val visibility: Visibility?,
) {
    companion object {
        const val IMAGE_THUMBNAIL_SIZE = 100
        const val IMAGE_LARGE_SIZE = 500
    }
}

fun PhotoModel.mapToPresentationModel() =
    Photo(
        thumbnailUrl = "${prefix}cap$IMAGE_THUMBNAIL_SIZE$suffix",
        url = "${prefix}cap$IMAGE_LARGE_SIZE$suffix",
        visibility = visibility
    )
