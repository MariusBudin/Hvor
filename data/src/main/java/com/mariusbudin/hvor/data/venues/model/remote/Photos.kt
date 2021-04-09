package com.mariusbudin.hvor.data.venues.model.remote

import com.mariusbudin.hvor.data.venues.model.PhotoEntity

data class Photos(
    val count: Int,
    val items: List<PhotoEntity>,
) {
    companion object {
        val empty = Photos(0, emptyList())
    }
}