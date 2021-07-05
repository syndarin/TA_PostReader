package com.vtiahotenkov.gorillastestassignment.di

import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.migration.DisableInstallInCheck
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer

@DisableInstallInCheck
@Module
class TestApiModule {

    @Reusable
    @Provides
    fun provideMockWebServer(): MockWebServer = MockWebServer()

    @Provides
    fun provideApolloClient(
        mockWebServer: MockWebServer
    ): ApolloClient {
        val interceptor = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return ApolloClient.builder()
            .serverUrl(mockWebServer.url("/"))
            .okHttpClient(client)
            .build()
    }
}