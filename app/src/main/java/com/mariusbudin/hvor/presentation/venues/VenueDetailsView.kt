package com.mariusbudin.hvor.presentation.venues

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.mariusbudin.hvor.core.extension.load
import com.mariusbudin.hvor.databinding.ViewVenueDetailsBinding
import com.mariusbudin.hvor.presentation.venues.model.Venue

class VenueDetailsView constructor(context: Context, attrs: AttributeSet?) :
    CardView(context, attrs) {
    private var binding: ViewVenueDetailsBinding =
        ViewVenueDetailsBinding.inflate(LayoutInflater.from(context), this, true)
    private var onClose: (() -> Unit)? = null

    init {
        binding.close.setOnClickListener { onClose?.invoke() }
    }

    fun update(venue: Venue) {
        venue.also {
            binding.title.text = it.name
            binding.image.load(if (it.photos.isNullOrEmpty()) it.mainCategoryIcon else it.photos[0].url)
        }
    }

    fun setOnCloseListener(onClose: () -> Unit) {
        this.onClose = onClose
    }
}
