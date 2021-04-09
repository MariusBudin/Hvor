package com.mariusbudin.hvor.presentation.venues

import com.mariusbudin.hvor.AndroidTest
import com.mariusbudin.hvor.core.functional.Either
import com.mariusbudin.hvor.domain.venues.GetVenues
import com.mariusbudin.hvor.domain.venues.model.VenueModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test

class VenuesViewModelTest : AndroidTest() {

    private lateinit var viewModel: VenuesViewModel

    @MockK
    private lateinit var getVenues: GetVenues

    @Before
    fun setUp() {
        viewModel = VenuesViewModel(getVenues)
    }

    @Test
    fun `loading venues should update live data`() {
        val rick = VenueModel(
            id = 1,
            name = "Rick",
            status = "Alive",
            location = "Earth",
            species = "human",
            image = "fake.url"
        )
        val morty = VenueModel(
            id = 1,
            name = "Morty",
            status = "Alive",
            location = "Earth",
            species = "human",
            image = "another.fake.url"
        )
        val venues = listOf(rick, morty)
        coEvery { getVenues.run(any()) } returns Either.Right(venues)

        viewModel.venues.observeForever {
            it!!.size shouldEqualTo 2
            it[0].id shouldEqualTo 0
            it[0].name shouldEqualTo "Rick"
            it[1].id shouldEqualTo 1
            it[1].species shouldEqualTo "human"
        }

        runBlocking { viewModel.getVenues() }
    }
}