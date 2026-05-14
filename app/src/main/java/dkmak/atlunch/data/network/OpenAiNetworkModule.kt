package dkmak.atlunch.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dkmak.atlunch.BuildConfig
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
annotation class OpenAiOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenAiRetrofit

@Module
@InstallIn(SingletonComponent::class)
object OpenAiNetworkModule {
    private const val BASE_URL = "https://api.openai.com/"

    @Provides
    @Singleton
    @OpenAiOkHttpClient
    fun provideOpenAiOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val updatedRequest =
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                        .build()
                chain.proceed(updatedRequest)
            }.apply {
                if (BuildConfig.DEBUG) {
                    addNetworkInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    )
                }
            }.build()

    @Provides
    @Singleton
    @OpenAiRetrofit
    fun provideOpenAiRetrofit(
        json: Json,
        @OpenAiOkHttpClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideOpenApiService(
        @OpenAiRetrofit retrofit: Retrofit,
    ): OpenApiService = retrofit.create(OpenApiService::class.java)

    @Provides
    @Singleton
    fun provideOpenAiClient(openApiService: OpenApiService): OpenAiClient = OpenAiClient(openApiService)
}
