package com.mariusbudin.hvor.data.venues.model.remote

data class Location(
    val address: String?,
    val crossStreet: String?,
    val lat: Double?,
    val lng: Double?,
    val distance: Int?,
    val city: String?,
    val state: String?,
    val country: String?,
    val formattedAddress: List<String>?,
    val isFuzzed: Boolean?,
) {
    companion object {
        val empty =
            Location("", "", 0.0, 0.0, 0, "", "", "", emptyList(), true)
    }
}
