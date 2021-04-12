package com.mariusbudin.hvor.data.venues

import com.mariusbudin.hvor.data.BuildConfig
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
        private const val VENUES_LIMIT = 3
        private const val PHOTOS_LIMIT = 1
        private const val RADIUS_LIMIT = 1000
        private const val VERSION = "20210409"
        private const val FOOD_CATEGORY = "categoryId=4d4b7105d754a06374d81259"
        private const val COMMON_PARAMS =
            "client_id=${BuildConfig.FOURSQUARE_CLIENT_ID}&client_secret=${BuildConfig.FOURSQUARE_CLIENT_SECRET}&v=$VERSION"
    }

    @GET("/v2/venues/search?$COMMON_PARAMS&limit=$VENUES_LIMIT&$FOOD_CATEGORY&radius=$RADIUS_LIMIT")
    fun venues(@Query("ll") latLng: String): Call<VenuesResponse>

    @GET("/v2/venues/{id}/photos?$COMMON_PARAMS&limit=$PHOTOS_LIMIT")
    fun venuePhotos(@Path("id") id: String): Call<PhotosResponse>

    class Service @Inject constructor(retrofit: Retrofit) : VenuesApi {
        private val api by lazy { retrofit.create(VenuesApi::class.java) }

        override fun venues(latLng: String) = api.venues(latLng)
        override fun venuePhotos(id: String) = api.venuePhotos(id)
    }
}