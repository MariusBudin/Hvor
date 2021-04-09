package com.mariusbudin.hvor.data.venues.di

import com.mariusbudin.hvor.data.common.AppDatabase
import com.mariusbudin.hvor.data.common.platform.NetworkHandler
import com.mariusbudin.hvor.data.venues.VenuesApi
import com.mariusbudin.hvor.data.venues.VenuesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VenuesDataModule {
    @Provides
    fun provideApi(retrofit: Retrofit): VenuesApi =
        retrofit.create(VenuesApi::class.java)

    @Singleton
    @Provides
    fun provideRepository(
        remote: VenuesRepository.Remote,
        local: VenuesRepository.Local
    ) = VenuesRepository(remote, local)

    @Singleton
    @Provides
    fun provideRemoteDataSource(
        networkHandler: NetworkHandler,
        api: VenuesApi
    ) = VenuesRepository.Remote(networkHandler, api)

    @Singleton
    @Provides
    fun provideLocalDataSource(db: AppDatabase) =
        VenuesRepository.Local(db.venuesDao())
}