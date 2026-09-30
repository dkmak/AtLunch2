package dkmak.atlunch.data.network.places

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dkmak.atlunch.data.network.bff.BffPlacesApiService
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesNetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    @Provides
    @Singleton
    fun providePlacesApiClient(bffPlacesApiService: BffPlacesApiService): PlacesApiClient = PlacesApiClient(bffPlacesApiService)
}
