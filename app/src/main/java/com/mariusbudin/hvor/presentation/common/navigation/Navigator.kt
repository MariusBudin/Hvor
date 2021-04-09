package com.mariusbudin.hvor.presentation.common.navigation

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mariusbudin.hvor.R

class Navigator {

    fun navigateToVenueDetails(
        fragment: Fragment,
        venueId: String
    ) {
//        fragment.findNavController().navigate(
//            R.id.action_venuesFragment_to_venueDetailsFragment,
//            bundleOf(PARAM_ID to venueId)
//        )
    }

    companion object {
        const val PARAM_ID = "params.id"
    }
}
