package com.mariusbudin.hvor.presentation.venues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariusbudin.hvor.R
import com.mariusbudin.hvor.core.extension.load
import com.mariusbudin.hvor.databinding.ItemVenueBinding
import com.mariusbudin.hvor.presentation.venues.model.Venue

class VenuesAdapter(
    private val onSelect: (venue: Venue) -> Unit
) : ListAdapter<Venue, VenuesAdapter.VenueViewHolder>(Venue.diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val binding: ItemVenueBinding =
            ItemVenueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VenueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) =
        holder.bind(getItem(position), onSelect)

    class VenueViewHolder(private val binding: ItemVenueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Venue,
            onSelect: (venue: Venue) -> Unit
        ) {
            with(item) {
                binding.title.text = name
                mainCategory?.let { binding.category.text = it }
                location.distance?.let {
                    binding.distance.text =
                        binding.root.resources.getString(R.string.venues_distance_meters, it)
                }

                binding.image.load(if (photos.isNullOrEmpty()) mainCategoryIcon else photos[0].url)
                itemView.setOnClickListener { onSelect(this) }
            }
        }
    }
}