package com.vtiahotenkov.gorillastestassignment.api

import com.apollographql.apollo.ApolloClient
import com.vtiahotenkov.gorillastestassignment.ApiUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    fun provideApolloClient(
        @ApiUrl url: String
    ): ApolloClient = ApolloClient.builder().serverUrl(url).build()
}