package com.mariusbudin.hvor.data.venues

import com.mariusbudin.hvor.core.exception.Failure
import com.mariusbudin.hvor.core.exception.Failure.NetworkConnection
import com.mariusbudin.hvor.core.functional.Either
import com.mariusbudin.hvor.core.functional.Either.Left
import com.mariusbudin.hvor.data.common.BaseRepository
import com.mariusbudin.hvor.data.common.platform.NetworkHandler
import com.mariusbudin.hvor.data.venues.model.PhotoEntity
import com.mariusbudin.hvor.data.venues.model.VenueEntity
import javax.inject.Inject

class VenuesRepository @Inject constructor(
    private val remote: Remote,
    private val local: Local
) {
    fun venues() = remote.venues()
    fun venuePhotos(id: String) = remote.venuePhotos(id)

    class Remote @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val api: VenuesApi
    ) : BaseRepository.Remote(networkHandler) {

        fun venues(): Either<Failure, List<VenueEntity>> {
            return when (networkHandler.isNetworkAvailable()) {
                true -> request(
                    api.venues(),
                    { it.response.venues },
                    emptyList()
                )
                false -> Left(NetworkConnection)
            }
        }

        fun venuePhotos(id: String): Either<Failure, List<PhotoEntity>> {
            return when (networkHandler.isNetworkAvailable()) {
                true -> request(
                    api.venuePhotos(id),
                    { it.response.photos.items },
                    emptyList()
                )
                false -> Left(NetworkConnection)
            }
        }
    }

    class Local @Inject constructor(
        private val dao: VenuesDao
    ) {
        fun venues() = dao.getAll()
        fun venue(id: Int) = dao.get(id)
        suspend fun storeVenues(venues: List<VenueEntity>) =
            dao.insertAll(venues)

        suspend fun storeVenue(venue: VenueEntity) = dao.insert(venue)
    }
}
