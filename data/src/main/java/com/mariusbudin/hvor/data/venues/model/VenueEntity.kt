package com.mariusbudin.hvor.data.venues.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mariusbudin.hvor.data.venues.model.remote.Category
import com.mariusbudin.hvor.data.venues.model.remote.Location

@Entity(tableName = "venues")
data class VenueEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: Location,
    val categories: List<Category>
) {
    companion object {
        val empty =
            VenueEntity("", "", Location.empty, emptyList())
    }
}
