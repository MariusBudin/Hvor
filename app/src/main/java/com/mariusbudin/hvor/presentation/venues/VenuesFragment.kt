package com.mariusbudin.hvor.presentation.venues

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mariusbudin.hvor.R
import com.mariusbudin.hvor.core.extension.hide
import com.mariusbudin.hvor.core.extension.show
import com.mariusbudin.hvor.core.platform.GpsTracker
import com.mariusbudin.hvor.core.platform.autoCleared
import com.mariusbudin.hvor.databinding.VenuesFragmentBinding
import com.mariusbudin.hvor.presentation.common.platform.BaseFragment
import com.mariusbudin.hvor.presentation.common.platform.getBitmapDescriptor
import com.mariusbudin.hvor.presentation.venues.model.Venue
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class VenuesFragment : BaseFragment(), OnMapReadyCallback {

    private var binding: VenuesFragmentBinding by autoCleared()
    private val viewModel: VenuesViewModel by viewModels()

    private lateinit var adapter: VenuesAdapter
    private var map: GoogleMap? = null
    private lateinit var locationPermissionResult: ActivityResultLauncher<Array<String>>
    private var gpsTracker: GpsTracker? = null

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
        binding.userLocation.setOnClickListener { checkLocation() }

        locationPermissionResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                when (it[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                    true -> {
                        gpsTracker?.getLocation()
                        binding.userLocation.hide()
                    }
                    else -> {
                        notify(R.string.failure_permission_denied)
                        binding.userLocation.show()
                    }
                }
            }

        onMoved() //move to a default location first

        context?.let {
            gpsTracker = GpsTracker(it)
            gpsTracker?.onNewLocationAvailable = { lat, lng ->
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lat, lng),
                        VENUES_LIST_ZOOM
                    )
                )
                onMoved(lat, lng)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLocation()
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
        binding.recycler.showShimmerAdapter()
    }

    private fun renderNoLocation() {
        binding.recycler.showShimmerAdapter()
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
        binding.recycler.hideShimmerAdapter()
    }

    override fun renderFailure(@StringRes message: Int) {
        binding.progress.hide()
        notifyWithAction(message, R.string.action_retry, ::onMoved)
    }

    private fun renderLeave() {
        Timber.d("Render leave")

        activity?.finish()
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
                    context?.getBitmapDescriptor(R.drawable.ic_baseline_location_pin_circle_24)
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

    private fun checkLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                gpsTracker?.getLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected.
                askForPermissions()
            }
            else -> {
                askForPermissions()
            }
        }
    }

    private fun askForPermissions() {
        locationPermissionResult.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gpsTracker?.stopUsingGPS()
    }

    companion object {
        private const val DEFAULT_LAT = 41.3865315403949
        private const val DEFAULT_LNG = 2.1694530688896734

        private const val VENUES_LIST_ZOOM = 12f
        private const val VENUE_DETAIL_ZOOM = 14f
    }
}