package com.mariusbudin.hvor.domain.venues.di

import com.mariusbudin.hvor.data.venues.VenuesRepository
import com.mariusbudin.hvor.domain.venues.GetVenuePhotos
import com.mariusbudin.hvor.domain.venues.GetVenues
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VenuesDomainModule {

    @Singleton
    @Provides
    fun provideGetVenues(repository: VenuesRepository) =
        GetVenues(repository)

    @Singleton
    @Provides
    fun provideGetVenuePhotos(repository: VenuesRepository) =
        GetVenuePhotos(repository)
}