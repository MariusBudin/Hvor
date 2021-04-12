package com.mariusbudin.hvor.data.venues.model.remote

data class Icon(
    val prefix: String,
    val suffix: String
) {
    companion object {
        val empty =
            Icon("", "")
    }
}