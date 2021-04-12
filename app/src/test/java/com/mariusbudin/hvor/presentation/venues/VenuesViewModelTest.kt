package com.mariusbudin.hvor.presentation.venues

import androidx.lifecycle.Observer
import com.mariusbudin.hvor.AndroidTest
import com.mariusbudin.hvor.core.functional.Either
import com.mariusbudin.hvor.data.venues.model.remote.Category
import com.mariusbudin.hvor.data.venues.model.remote.Location
import com.mariusbudin.hvor.domain.venues.GetVenuePhotos
import com.mariusbudin.hvor.domain.venues.GetVenues
import com.mariusbudin.hvor.domain.venues.model.VenueModel
import com.mariusbudin.hvor.presentation.venues.model.Venue
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

class VenuesViewModelTest : AndroidTest() {

    private lateinit var viewModel: VenuesViewModel

    @MockK
    private lateinit var getVenues: GetVenues

    @MockK
    private lateinit var getVenuePhotos: GetVenuePhotos

    @Before
    fun setUp() {
        viewModel = VenuesViewModel(getVenues, getVenuePhotos)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `loading venues should update live data`() {
        val venueModels = listOf(
            VenueModel(
                id = "some_id_x345",
                name = "Cohi Bar",
                location = Location.empty,
                categories = listOf(Category.empty)
            ), VenueModel(
                id = "some_id_x345h54",
                name = "Frank Restaurant",
                location = Location.empty.copy(address = "Fake Street, 21"),
                categories = listOf(Category.empty)
            )
        )
        val venues = listOf(
            Venue(
                id = "some_id_x345",
                name = "Cohi Bar",
                location = Location.empty,
                categories = listOf(Category.empty),
                mainCategory = "",
                photos = emptyList(),
                mainCategoryIcon = ""
            ), Venue(
                id = "some_id_x345h54",
                name = "Frank Restaurant",
                location = Location.empty.copy(address = "Fake Street, 21"),
                categories = listOf(Category.empty),
                mainCategory = "",
                photos = emptyList(),
                mainCategoryIcon = ""
            )
        )
        coEvery { getVenues.run(any()) } returns Either.Right(venueModels)

        val observer = mockk<Observer<VenuesState>>()
        val slot = slot<VenuesState>()
        val list = arrayListOf<VenuesState>()

        viewModel.state.observeForever(observer)

        every { observer.onChanged(capture(slot)) } answers {
            list.add(slot.captured)
        }

        runBlockingTest {
            viewModel.onMoved(DEFAULT_LAT, DEFAULT_LNG)
        }

        assertEquals(
            list, listOf(
                VenuesState.LoadingVenues,
            )
        )
    }

    companion object {
        const val DEFAULT_LAT = 41.3865315403949
        const val DEFAULT_LNG = 2.1694530688896734
    }
}