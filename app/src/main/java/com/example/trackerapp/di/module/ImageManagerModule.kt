package com.example.trackerapp.di.module

import com.example.trackerapp.utils.ImageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ImageManagerModule {
    @Provides
    @Singleton
    fun provideImageManager(): ImageManager = ImageManager()
}