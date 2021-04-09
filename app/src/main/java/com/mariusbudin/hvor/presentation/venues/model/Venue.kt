package com.mariusbudin.hvor.presentation.venues.model

import androidx.recyclerview.widget.DiffUtil
import com.mariusbudin.hvor.data.venues.model.remote.Category
import com.mariusbudin.hvor.data.venues.model.remote.Location
import com.mariusbudin.hvor.domain.venues.model.VenueModel
import com.mariusbudin.hvor.presentation.venues.model.Venue.Companion.ICON_SIZE

data class Venue(
    val id: String,
    val name: String,
    val location: Location,
    val mainCategory: String?,
    val mainCategoryIcon: String?,
    val categories: List<Category>,
    val photos: List<Photo>?
) {
    companion object {
        const val ICON_SIZE = 88

        var diffCallback: DiffUtil.ItemCallback<Venue> =
            object : DiffUtil.ItemCallback<Venue>() {
                override fun areItemsTheSame(oldItem: Venue, newItem: Venue): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Venue, newItem: Venue): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

fun VenueModel.mapToPresentationModel() =
    Venue(
        id = id,
        name = name,
        location = location,
        mainCategory = categories.firstOrNull { it.primary }?.shortName,
        mainCategoryIcon = categories.firstOrNull { it.primary }?.mainCategoryIcon(),
        categories = categories,
        photos = null
    )

fun Category.mainCategoryIcon() = "${icon.prefix}$ICON_SIZE${icon.suffix}"
