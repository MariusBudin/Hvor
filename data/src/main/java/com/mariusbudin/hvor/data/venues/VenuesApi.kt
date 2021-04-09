package com.mariusbudin.hvor.data.venues

import com.mariusbudin.hvor.data.venues.model.remote.PhotosResponse
import com.mariusbudin.hvor.data.venues.model.remote.VenuesResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

interface VenuesApi {

    companion object {
        private const val VENUES_LIMIT = 10
        private const val PHOTOS_LIMIT = 3
        private const val CLIENT_ID = "MN1JO4MZMERMXGNBQSNTBAKY1EVXFGUWWP1QGSVLEJSSBZ5R"
        private const val CLIENT_SECRET = "KSTCF5SWUBLJZLDL4UZFC2T5QYSHWDIEPW5TF3RY3CWZQUW0"
        private const val VERSION = "20210409"
        private const val COMMON_PARAMS =
            "&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&v=$VERSION"

        private const val DEFAULT_LAT_LONG = "41.49915233907989, 2.157568450840408"
    }

    @GET("/v2/venues/search?$COMMON_PARAMS&limit=$VENUES_LIMIT&query=food")
    fun venues(@Query("ll") latLong: String = DEFAULT_LAT_LONG): Call<VenuesResponse>

    @GET("/v2/venues/{id}/photos?$COMMON_PARAMS&limit=$PHOTOS_LIMIT")
    fun venuePhotos(@Path("id") id: String): Call<PhotosResponse>

    class Service @Inject constructor(retrofit: Retrofit) : VenuesApi {
        private val api by lazy { retrofit.create(VenuesApi::class.java) }

        override fun venues(latLong: String) = api.venues()
        override fun venuePhotos(id: String) = api.venuePhotos(id)
    }
}