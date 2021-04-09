package com.mariusbudin.hvor.domain.venues.model

import com.mariusbudin.hvor.data.venues.model.PhotoEntity
import com.mariusbudin.hvor.data.venues.model.Visibility

data class PhotoModel(
    val prefix: String,
    val suffix: String,
    val width: Int,
    val height: Int,
    val visibility: Visibility?,
)

fun PhotoEntity.mapToDomainModel() =
    PhotoModel(
        prefix = prefix,
        suffix = suffix,
        width = width,
        height = height,
        visibility = visibility
    )
