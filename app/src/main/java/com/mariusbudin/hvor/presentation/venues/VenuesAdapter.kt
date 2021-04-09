package com.mariusbudin.hvor.presentation.venues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariusbudin.hvor.core.extension.load
import com.mariusbudin.hvor.core.extension.loadCircle
import com.mariusbudin.hvor.databinding.ItemVenueBinding
import com.mariusbudin.hvor.presentation.venues.model.Venue

class VenuesAdapter(
    private val onSelect: (id: String) -> Unit
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
            onSelect: (id: String) -> Unit
        ) {
            with(item) {
                binding.title.text = name

                if (photos.isNullOrEmpty()) {
                    binding.image.load(mainCategoryIcon)
                } else {
                    binding.image.loadCircle(photos[0].thumbnailUrl)
                }
                itemView.setOnClickListener { onSelect(id) }
            }
        }
    }
}