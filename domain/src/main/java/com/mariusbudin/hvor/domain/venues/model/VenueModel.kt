package com.mariusbudin.hvor.domain.venues.model

import com.mariusbudin.hvor.data.venues.model.VenueEntity
import com.mariusbudin.hvor.data.venues.model.remote.Category
import com.mariusbudin.hvor.data.venues.model.remote.Location

data class VenueModel(
    val id: String,
    val name: String,
    val location: Location,
    val categories: List<Category>
)

fun VenueEntity.mapToDomainModel() =
    VenueModel(
        id = id,
        name = name,
        location = location,
        categories = categories,
    )
