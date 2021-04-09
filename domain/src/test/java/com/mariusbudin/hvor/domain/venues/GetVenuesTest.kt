package com.mariusbudin.hvor.domain.venues

import com.mariusbudin.hvor.core.domain.UseCase
import com.mariusbudin.hvor.core.functional.Either
import com.mariusbudin.hvor.data.venues.VenuesRepository
import com.mariusbudin.hvor.data.venues.model.VenueEntity
import com.mariusbudin.hvor.domain.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetVenuesTest : UnitTest() {

    private lateinit var getVenues: GetVenues

    @MockK
    private lateinit var repository: VenuesRepository

    @Before
    fun setUp() {
        getVenues = GetVenues(repository)
        every { repository.venues() } returns Either.Right(listOf(VenueEntity.empty))
    }

    @Test
    fun `should get data from repository`() {
        runBlocking { getVenues.run(UseCase.None()) }

        verify(exactly = 1) { repository.venues() }
    }
}
