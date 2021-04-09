package com.mariusbudin.hvor.data.common

import androidx.room.TypeConverter
import com.mariusbudin.hvor.data.venues.model.remote.Category
import com.mariusbudin.hvor.data.venues.model.remote.Location

/**
 * Not a real converter as we're not using the Database for this version, it's meant as an example
 * of its placement in this particular architecture
 */
class Converters {

    @TypeConverter
    fun toLocation(value: String) = Location.empty

    @TypeConverter
    fun fromLocation(value: Location) = ""

    @TypeConverter
    fun toCategories(value: String) = emptyList<Category>()

    @TypeConverter
    fun fromCategories(value: List<Category>) = ""
}