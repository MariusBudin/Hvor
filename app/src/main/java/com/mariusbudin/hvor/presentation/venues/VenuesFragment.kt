package com.mariusbudin.hvor.presentation.venues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
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
import com.mariusbudin.hvor.core.platform.autoCleared
import com.mariusbudin.hvor.databinding.GenericListFragmentBinding
import com.mariusbudin.hvor.presentation.common.platform.BaseFragment
import com.mariusbudin.hvor.presentation.venues.model.Venue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VenuesFragment : BaseFragment(), OnMapReadyCallback {

    private var binding: GenericListFragmentBinding by autoCleared()
    private val viewModel: VenuesViewModel by viewModels()

    private lateinit var adapter: VenuesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GenericListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        setupRecyclerView()
        setupObservers()
        getVenues()
    }

    private fun setupRecyclerView() {
        adapter = VenuesAdapter { navigator.navigateToVenueDetails(this, it) }
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recycler.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.venues.observe(viewLifecycleOwner, ::renderVenues)
        viewModel.failure.observe(viewLifecycleOwner, ::handleFailure)
    }

    private fun getVenues() {
        binding.progress.hide()
        viewModel.getVenues()
    }

    private fun renderVenues(venues: List<Venue>?) {
        adapter.submitList(venues)
        binding.progress.hide()
    }

    override fun renderFailure(@StringRes message: Int) {
        binding.progress.hide()
        notifyWithAction(message, R.string.action_retry, ::getVenues)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.apply {
            val currentLocation = LatLng(41.3865315403949, 2.1694530688896734)
            addMarker(
                MarkerOptions()
                    .position(currentLocation)
                    .title("I'm here now")
            )
            animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
            setPadding(16,0,0,450) //FIXME use this dynamically
        }
    }

}