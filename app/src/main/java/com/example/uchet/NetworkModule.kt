package com.example.uchet

import android.accounts.NetworkErrorException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    internal fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    internal fun provideHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) { json(json) }

        install(HttpTimeout) {
            socketTimeoutMillis = 60_000
        }

        engine {
            config {
                connectTimeout(0, TimeUnit.SECONDS)
            }
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response -> !response.status.isSuccess() }
            retryOnExceptionIf { _, cause -> cause is NetworkErrorException }
            retryOnServerErrors()
            exponentialDelay()
        }
    }
}