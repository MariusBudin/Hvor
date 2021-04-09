package com.mariusbudin.hvor.data.venues.model.remote

import com.mariusbudin.hvor.data.venues.model.VenueEntity

data class VenuesWrapper(
    val venues: List<VenueEntity>,
) {
    companion object {
        val empty = VenuesWrapper(emptyList())
    }
}