package com.mariusbudin.hvor.data.venues.model.remote

data class VenuesResponse(
    val meta: Meta,
    val response: VenuesWrapper
) {
    companion object {
        val empty = VenuesResponse(Meta.empty, VenuesWrapper.empty)
    }
}