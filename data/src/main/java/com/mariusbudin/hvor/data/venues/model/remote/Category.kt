package com.mariusbudin.hvor.data.venues.model.remote

data class Category(
    val id: String,
    val name: String,
    val shortName: String,
    val suffix: String,
    val icon: Icon,
    val primary: Boolean
)