package com.mariusbudin.hvor.presentation.venues

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mariusbudin.hvor.R
import com.mariusbudin.hvor.core.extension.hide
import com.mariusbudin.hvor.core.extension.show
import com.mariusbudin.hvor.core.platform.autoCleared
import com.mariusbudin.hvor.databinding.VenuesFragmentBinding
import com.mariusbudin.hvor.presentation.common.platform.BaseFragment
import com.mariusbudin.hvor.presentation.venues.model.Venue
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class VenuesFragment : BaseFragment(), OnMapReadyCallback {

    private var binding: VenuesFragmentBinding by autoCleared()
    private val viewModel: VenuesViewModel by viewModels()

    private lateinit var adapter: VenuesAdapter
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VenuesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        setupRecyclerView()
        setupObservers()

        activity?.onBackPressedDispatcher?.addCallback { viewModel.onBack() }
        binding.venueDetails.setOnCloseListener { viewModel.onBack() }
        onMoved()
    }

    private fun setupRecyclerView() {
        adapter = VenuesAdapter(::onVenueSelected)
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recycler.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, ::onNewState)
        viewModel.failure.observe(viewLifecycleOwner, ::handleFailure)
    }

    private fun onMoved(lat: Double = DEFAULT_LAT, lng: Double = DEFAULT_LNG) {
        binding.progress.hide()
        viewModel.onMoved(lat, lng)
    }

    private fun onVenueSelected(venue: Venue) {
        viewModel.onVenueSelected(venue)
    }

    private fun onNewState(state: VenuesState) {
        when (state) {
            is VenuesState.Error -> state.failure?.let { handleFailure(it) }
            is VenuesState.Leave -> renderLeave()
            VenuesState.LoadingVenues -> renderLoadingVenues()
            VenuesState.NoLocation -> renderNoLocation()
            is VenuesState.NavigationUpdate -> renderVenueDetailsUpdate(
                state.selectedVenue,
                state.otherVenues
            )
            is VenuesState.Exploration -> renderVenues(state.data)
            is VenuesState.Navigation -> renderVenueDetails(
                state.selectedVenue,
                state.otherVenues
            )
        }
    }

    private fun renderLoadingVenues() {
        Timber.d("Render loading")
    }

    private fun renderNoLocation() {
        Timber.d("Render no location")
    }

    private fun renderVenueDetails(detailVenue: Venue, otherVenues: List<Venue>?) {
        Timber.d("Render details for ${detailVenue.name}\tothers: ${otherVenues?.map { it.name }}")

        updateVenuesList(otherVenues)
        binding.userMarker.hide()
        removeAllMarkers()
        addVenueMarker(detailVenue)
        binding.venueDetails.show()
        binding.venueDetails.update(detailVenue)
    }

    private fun renderVenueDetailsUpdate(detailVenue: Venue, otherVenues: List<Venue>?) {
        Timber.d("Render details updated for ${detailVenue.name}\tothers: ${otherVenues?.map { it.name }}")
        updateVenuesList(otherVenues)
        binding.venueDetails.update(detailVenue)
    }

    private fun renderVenues(venues: List<Venue>?) {
        binding.userMarker.show()
        binding.venueDetails.hide()
        removeAllMarkers()
        updateVenuesList(venues)
    }

    override fun renderFailure(@StringRes message: Int) {
        binding.progress.hide()
        notifyWithAction(message, R.string.action_retry, ::onMoved)
    }

    private fun renderLeave() {
        Timber.d("Render leave")

        activity?.onBackPressed()
    }

    private fun updateVenuesList(venues: List<Venue>?) {
        Timber.d("Render venues: ${venues?.map { it.name }}")

        adapter.submitList(venues)
        binding.progress.hide()
        if (!venues.isNullOrEmpty()) {
            binding.recycler.smoothScrollToPosition(0)
        }
    }

    private fun addVenueMarker(venue: Venue) {
        if (venue.location.lat != null && venue.location.lng != null) {
            val location = LatLng(venue.location.lat!!, venue.location.lng!!)
            map?.addMarker(
                MarkerOptions().position(location).icon(
                    getBitmapDescriptor(R.drawable.ic_baseline_location_pin_circle_24)
                )
            )
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, VENUE_DETAIL_ZOOM))
        }
    }

    private fun removeAllMarkers() {
        map?.clear()
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        map?.apply {
            val currentLocation = LatLng(41.3865315403949, 2.1694530688896734)

            animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, VENUES_LIST_ZOOM))
            setPadding(16, 450, 0, 450) //FIXME use this dynamically

            setOnCameraMoveStartedListener { reason ->
                when (reason) {
                    GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                        Timber.d("The user started moving the map.")
                    }
                }
            }
            setOnCameraIdleListener {
                val latLng: LatLng = map.cameraPosition.target
                Timber.d("The user stopped moving the map at $latLng")
                onMoved(latLng.latitude, latLng.longitude)
            }
        }
    }

    private fun getBitmapDescriptor(@DrawableRes id: Int): BitmapDescriptor? {
        val vectorDrawable = getDrawable(requireContext(), id) as VectorDrawable
        val h = vectorDrawable.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth
        vectorDrawable.setBounds(0, 0, w, h)
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bm)
    }

    companion object {
        private const val DEFAULT_LAT = 41.3865315403949
        private const val DEFAULT_LNG = 2.1694530688896734

        private const val VENUES_LIST_ZOOM = 12f
        private const val VENUE_DETAIL_ZOOM = 14f
    }
}