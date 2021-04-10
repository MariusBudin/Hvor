package com.mariusbudin.hvor.presentation.venues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mariusbudin.hvor.core.domain.UseCase
import com.mariusbudin.hvor.core.platform.BaseViewModel
import com.mariusbudin.hvor.domain.venues.GetVenuePhotos
import com.mariusbudin.hvor.domain.venues.GetVenues
import com.mariusbudin.hvor.domain.venues.model.PhotoModel
import com.mariusbudin.hvor.domain.venues.model.VenueModel
import com.mariusbudin.hvor.presentation.venues.model.Venue
import com.mariusbudin.hvor.presentation.venues.model.mapToPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VenuesViewModel @Inject constructor(
    private val getVenues: GetVenues,
    private val getVenuePhotos: GetVenuePhotos
) : BaseViewModel() {

    private val _venues: MutableLiveData<List<Venue>> = MutableLiveData()
    val venues: LiveData<List<Venue>> = _venues

    fun getVenues() {
        getVenues(UseCase.None(), viewModelScope) {
            it.fold(
                ::handleFailure,
                ::handleVenues
            )
        }
    }

    private fun handleVenues(venues: List<VenueModel>) {
        val newVenues = venues.map(VenueModel::mapToPresentationModel)
        _venues.value = newVenues
//        newVenues.forEach { getVenuePhotos(it) }
    }

    /*
     * TODO: notify from the view to only call when scrolled to position
     *  and avoid calls for items the user might not scroll to
     */
    fun onDisplayPosition(position: Int) {
        _venues.value?.get(position)?.let { venue ->
            if (venue.photos == null) { //check if photos are already cached
                getVenuePhotos(venue)
            }
        }
    }

    private fun getVenuePhotos(venue: Venue) {
        getVenuePhotos(GetVenuePhotos.Params(venue.id), viewModelScope) { result ->
            result.fold(::handleFailure) { handleVenuePhotos(venue.id, it) }
        }
    }

    private fun handleVenuePhotos(venueId: String, photos: List<PhotoModel>) {
        val venuePhotos = photos.map(PhotoModel::mapToPresentationModel)
        _venues.value?.let { currentVenues ->
            val newVenues = ArrayList(currentVenues)
            val venueIndex = currentVenues.indexOfFirst { venueId == it.id }
            newVenues.set(venueIndex, currentVenues[venueIndex].copy(photos = venuePhotos))
            _venues.value = newVenues
        }
    }
}