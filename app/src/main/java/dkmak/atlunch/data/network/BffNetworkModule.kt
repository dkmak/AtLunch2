package dkmak.atlunch.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dkmak.atlunch.BuildConfig
import dkmak.atlunch.data.network.places.BffPlacesApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BffRetrofit

@Module
@InstallIn(SingletonComponent::class)
object BffNetworkModule {
    @Provides
    @Singleton
    @BffRetrofit
    fun provideBffRetrofit(json: Json): Retrofit {
        val client =
            OkHttpClient
                .Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                            HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BASIC
                            },
                        )
                    }
                }.build()
        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.BFF_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideBffPlacesApiService(
        @BffRetrofit retrofit: Retrofit,
    ): BffPlacesApiService = retrofit.create(BffPlacesApiService::class.java)
}
