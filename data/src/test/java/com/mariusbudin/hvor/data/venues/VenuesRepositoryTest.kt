package com.mariusbudin.hvor.data.venues

import com.mariusbudin.hvor.core.exception.Failure
import com.mariusbudin.hvor.core.functional.Either
import com.mariusbudin.hvor.data.UnitTest
import com.mariusbudin.hvor.data.common.platform.NetworkHandler
import com.mariusbudin.hvor.data.venues.model.Status
import com.mariusbudin.hvor.data.venues.model.VenueEntity
import com.mariusbudin.hvor.data.venues.model.remote.*
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class VenuesRepositoryTest : UnitTest() {

    private lateinit var repository: VenuesRepository.Remote

    @MockK
    private lateinit var networkHandler: NetworkHandler

    @MockK
    private lateinit var api: VenuesApi

    @MockK
    private lateinit var venuesCall: Call<VenuesResponse>

    @MockK
    private lateinit var venuesResponse: Response<VenuesResponse>

    @Before
    fun setUp() {
        repository = VenuesRepository.Remote(networkHandler, api)
    }

    @Test
    fun `should return empty list by default`() {
        every { networkHandler.isNetworkAvailable() } returns true
        every { venuesResponse.body() } returns null
        every { venuesResponse.isSuccessful } returns true
        every { venuesCall.execute() } returns venuesResponse
        every { api.venues() } returns venuesCall

        val venues = repository.venues()

        venues shouldEqual Either.Right(emptyList<VenueEntity>())
        verify(exactly = 1) { api.venues() }
    }

    @Test
    fun `should return venues list from api`() {
        val venue = VenueEntity(
            id = 1,
            name = "Rick",
            status = Status.ALIVE,
            species = "human",
            VenueLocationEntity.empty,
            image = "fake.url"
        )

        every { networkHandler.isNetworkAvailable() } returns true
        every { venuesResponse.body() } returns VenuesResponse(
            InfoRemoteModel.empty,
            listOf(venue)
        )
        every { venuesResponse.isSuccessful } returns true
        every { venuesCall.execute() } returns venuesResponse
        every { api.venues() } returns venuesCall

        val venues = repository.venues()

        venues shouldEqual Either.Right(listOf(venue))
        verify(exactly = 1) { api.venues() }
    }

    @Test
    fun `api should return network failure when no connection`() {
        every { networkHandler.isNetworkAvailable() } returns false

        val venues = repository.venues()

        venues shouldBeInstanceOf Either::class.java
        venues.isLeft shouldEqual true
        venues.fold(
            { failure -> failure shouldBeInstanceOf Failure.NetworkConnection::class.java },
            {})
        verify { api wasNot Called }
    }

    @Test
    fun `api should return server error if no successful response`() {
        every { networkHandler.isNetworkAvailable() } returns true
        every { venuesResponse.isSuccessful } returns false
        every { venuesCall.execute() } returns venuesResponse
        every { api.venues() } returns venuesCall

        val venues = repository.venues()

        venues shouldBeInstanceOf Either::class.java
        venues.isLeft shouldEqual true
        venues.fold(
            { failure -> failure shouldBeInstanceOf Failure.ServerError::class.java },
            {})
    }

    @Test
    fun `request should catch exceptions`() {
        every { networkHandler.isNetworkAvailable() } returns true
        every { venuesCall.execute() } returns venuesResponse
        every { api.venues() } returns venuesCall

        val venues = repository.venues()

        venues shouldBeInstanceOf Either::class.java
        venues.isLeft shouldEqual true
        venues.fold(
            { failure -> failure shouldBeInstanceOf Failure.ServerError::class.java },
            {})
    }
}