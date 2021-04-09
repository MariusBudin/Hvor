package com.mariusbudin.hvor.data.venues.model

import com.squareup.moshi.Json

data class PhotoEntity(
    val prefix: String,
    val suffix: String,
    val width: Int,
    val height: Int,
    val visibility: Visibility? = Visibility.PRIVATE,
)

enum class Visibility {
    @Json(name = "public")
    PUBLIC,

    @Json(name = "friends")
    FRIENDS,

    @Json(name = "private")
    PRIVATE
}