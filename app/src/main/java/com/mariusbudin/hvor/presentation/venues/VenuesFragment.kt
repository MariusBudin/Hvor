package com.mariusbudin.hvor.presentation.venues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariusbudin.hvor.R
import com.mariusbudin.hvor.core.extension.hide
import com.mariusbudin.hvor.core.platform.autoCleared
import com.mariusbudin.hvor.databinding.GenericListFragmentBinding
import com.mariusbudin.hvor.presentation.common.platform.BaseFragment
import com.mariusbudin.hvor.presentation.venues.model.Venue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VenuesFragment : BaseFragment() {

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
        setupRecyclerView()
        setupObservers()
        getVenues()
    }

    private fun setupRecyclerView() {
        adapter = VenuesAdapter { navigator.navigateToVenueDetails(this, it) }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
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
}