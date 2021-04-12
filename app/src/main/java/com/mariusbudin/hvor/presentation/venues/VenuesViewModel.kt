package com.mariusbudin.hvor.presentation.venues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mariusbudin.hvor.core.platform.BaseViewModel
import com.mariusbudin.hvor.domain.venues.GetVenuePhotos
import com.mariusbudin.hvor.domain.venues.GetVenues
import com.mariusbudin.hvor.domain.venues.model.PhotoModel
import com.mariusbudin.hvor.domain.venues.model.VenueModel
import com.mariusbudin.hvor.presentation.venues.model.Photo
import com.mariusbudin.hvor.presentation.venues.model.Venue
import com.mariusbudin.hvor.presentation.venues.model.mapToPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VenuesViewModel @Inject constructor(
    private val getVenues: GetVenues,
    private val getVenuePhotos: GetVenuePhotos
) : BaseViewModel() {

    private var currentLat: Double? = null
    private var currentLng: Double? = null
    private var currentVenues: List<Venue>? = null

    private val _state: MutableLiveData<VenuesState> = MutableLiveData()
    val state: LiveData<VenuesState> = _state

    fun onMoved(lat: Double, lng: Double) {
        currentLat = lat
        currentLng = lng

        if (_state.value !is VenuesState.Navigation) {
            getVenues(lat, lng)
        }
    }

    fun onVenueSelected(venue: Venue) {
        currentVenues?.let { venues ->
            _state.value = VenuesState.Navigation(venue, venues.filter { it.id != venue.id })
        }
    }

    /*
 * TODO: notify from the view to only call when scrolled to position
 *  and avoid calls for items the user might not scroll to
 */
    fun onDisplayPosition(position: Int) {

    }

    fun onBack() {
        when (_state.value) {
            is VenuesState.Navigation -> _state.value =
                currentVenues?.let { VenuesState.Exploration(it) }
            else -> _state.value = VenuesState.Leave
        }
    }

    private fun getVenues(lat: Double, lng: Double) {
        _state.value = VenuesState.LoadingVenues

        getVenues(GetVenues.Params(lat, lng), viewModelScope) { result ->
            result.fold(::handleFailure) { handleVenues(it, lat, lng) }
        }
    }

    private fun handleVenues(venues: List<VenueModel>, lat: Double, lng: Double) {
        // check if the result is for the current location, if the call took too much,
        // we might get results in a different order. Skip older values for now
        if (currentLat == lat && currentLng == lng) {
            val newVenues = venues.map(VenueModel::mapToPresentationModel)
            currentVenues = newVenues

            _state.value = VenuesState.Exploration(newVenues)

//            newVenues.forEach { getVenuePhotos(it) }
        }
    }

    private fun getVenuePhotos(venue: Venue) {
        getVenuePhotos(GetVenuePhotos.Params(venue.id), viewModelScope) { result ->
            result.fold({
                //do nothing for now in case of error, the API quota is really low
            }) { handleVenuePhotos(venue.id, it) }
        }
    }

    private fun handleVenuePhotos(venueId: String, photos: List<PhotoModel>) {
        val venuePhotos = photos.map(PhotoModel::mapToPresentationModel)

        when (val currentState = _state.value) {
            is VenuesState.Exploration -> {
                val newVenues = updateVenuePhotos(currentState.data, venueId, venuePhotos)
                if (currentState.data.any { it.id == venueId }) {
                    currentVenues = newVenues

                    _state.value = VenuesState.Exploration(newVenues)
                }
            }
            // check if we asked for the photos while in list but by th time they came back,
            // we're showing the details
            is VenuesState.Navigation -> {
                val otherVenues = updateVenuePhotos(currentState.otherVenues, venueId, venuePhotos)
                if (currentState.selectedVenue.id == venueId) {
                    val newVenue = currentState.selectedVenue.copy(photos = venuePhotos)
                    _state.value = VenuesState.NavigationUpdate(newVenue, otherVenues)
                }
            }
            else -> {/*ignore for now*/
            }
        }
    }

    private fun updateVenuePhotos(
        venues: List<Venue>,
        venueId: String,
        venuePhotos: List<Photo>
    ): ArrayList<Venue> {
        val venueIndex = venues.indexOfFirst { venueId == it.id }
        val newVenues = ArrayList(venues)
        if (venueIndex != -1) {
            newVenues[venueIndex] = venues[venueIndex].copy(photos = venuePhotos)
        }
        return newVenues
    }
}
