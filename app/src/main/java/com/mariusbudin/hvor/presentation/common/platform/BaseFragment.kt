package com.mariusbudin.hvor.presentation.common.platform

import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mariusbudin.hvor.R
import com.mariusbudin.hvor.core.exception.Failure
import com.mariusbudin.hvor.presentation.common.navigation.Navigator
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator

    abstract fun renderFailure(@StringRes message: Int)

    protected fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> renderFailure(R.string.failure_network_connection)
            is Failure.ServerError -> renderFailure(R.string.failure_server_error)
            else -> renderFailure(R.string.failure_generic_error)
        }
    }

    internal fun notify(@StringRes message: Int) =
        Snackbar.make(container(), message, Snackbar.LENGTH_SHORT).show()

    internal fun notify(message: String) =
        Snackbar.make(container(), message, Snackbar.LENGTH_SHORT).show()

    internal fun notifyWithAction(
        @StringRes message: Int,
        @StringRes actionText: Int,
        action: () -> Any
    ) {
        val snackBar = Snackbar.make(
            container(),
            message,
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(actionText) { action.invoke() }
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        snackBar.show()
    }

    private fun container() = requireActivity().findViewById<View>(android.R.id.content)
}
