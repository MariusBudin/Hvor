package com.mariusbudin.hvor.data.venues.model.remote

data class PhotosResponse(
    val meta: Meta,
    val response: PhotosWrapper
) {
    companion object {
        val empty = PhotosResponse(Meta.empty, PhotosWrapper.empty)
    }
}