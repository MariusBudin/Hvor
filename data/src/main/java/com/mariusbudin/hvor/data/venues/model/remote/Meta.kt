package com.mariusbudin.hvor.data.venues.model.remote

data class Meta(
    val code: Int,
    val requestId: String,
    val errorType: String?,
    val errorDetail: String?
) {
    companion object {
        val empty =
            Meta(200, "", "", "")
    }
}
