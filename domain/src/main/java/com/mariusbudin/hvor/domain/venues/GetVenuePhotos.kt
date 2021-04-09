package com.mariusbudin.hvor.domain.venues

import com.mariusbudin.hvor.core.domain.UseCase
import com.mariusbudin.hvor.core.functional.map
import com.mariusbudin.hvor.data.venues.VenuesRepository
import com.mariusbudin.hvor.data.venues.model.PhotoEntity
import com.mariusbudin.hvor.domain.venues.model.PhotoModel
import com.mariusbudin.hvor.domain.venues.model.mapToDomainModel
import javax.inject.Inject

class GetVenuePhotos
@Inject constructor(private val repository: VenuesRepository) :
    UseCase<List<PhotoModel>, GetVenuePhotos.Params>() {

    override suspend fun run(params: Params) =
        repository.venuePhotos(params.id).map { it.map(PhotoEntity::mapToDomainModel) }

    data class Params(val id: String)
}
