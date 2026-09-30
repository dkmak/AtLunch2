package dkmak.atlunch.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dkmak.atlunch.data.network.bff.BffRetrofit
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenAiNetworkModule {
    @Provides
    @Singleton
    fun provideOpenApiService(
        @BffRetrofit retrofit: Retrofit,
    ): OpenApiService = retrofit.create(OpenApiService::class.java)

    @Provides
    @Singleton
    fun provideOpenAiClient(openApiService: OpenApiService): OpenAiClient = OpenAiClient(openApiService)
}
