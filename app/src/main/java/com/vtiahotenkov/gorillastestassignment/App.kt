package com.vtiahotenkov.gorillastestassignment

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}

annotation class ApiUrl

annotation class PostPreviewCharsLimit

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @ApiUrl
    @Provides
    fun provideApiUrl(): String = BuildConfig.API_URL

    @PostPreviewCharsLimit
    @Provides
    fun providePostPreviewCharsLimit(): Int = BuildConfig.POST_PREVIEW_CHARS_LIMIT
}