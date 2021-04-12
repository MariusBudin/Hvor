package com.mariusbudin.hvor.domain.venues

import com.mariusbudin.hvor.core.domain.UseCase
import com.mariusbudin.hvor.core.functional.map
import com.mariusbudin.hvor.data.venues.VenuesRepository
import com.mariusbudin.hvor.data.venues.model.VenueEntity
import com.mariusbudin.hvor.domain.venues.model.VenueModel
import com.mariusbudin.hvor.domain.venues.model.mapToDomainModel
import javax.inject.Inject

class GetVenues
@Inject constructor(private val repository: VenuesRepository) :
    UseCase<List<VenueModel>, GetVenues.Params>() {

    override suspend fun run(params: Params) =
        repository.venues("${params.lat},${params.lng}")
            .map { it.map(VenueEntity::mapToDomainModel) }

    data class Params(val lat: Double, val lng: Double)
}
