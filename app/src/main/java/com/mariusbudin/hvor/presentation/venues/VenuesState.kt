package com.mariusbudin.hvor.presentation.venues

import com.mariusbudin.hvor.core.exception.Failure
import com.mariusbudin.hvor.presentation.venues.model.Venue

sealed class VenuesState {
    object NoLocation : VenuesState()
    object LoadingVenues : VenuesState()
    object Leave : VenuesState()
    data class Error(val failure: Failure?) : VenuesState()
    data class Exploration(val data: List<Venue>) : VenuesState()
    data class Navigation(val selectedVenue: Venue, val otherVenues: List<Venue>) : VenuesState()
    data class NavigationUpdate(val selectedVenue: Venue, val otherVenues: List<Venue>) :
        VenuesState()
}