package com.mariusbudin.hvor.data.venues.model.remote

data class PhotosWrapper(
    val photos: Photos,
) {
    companion object {
        val empty = PhotosWrapper(Photos.empty)
    }
}