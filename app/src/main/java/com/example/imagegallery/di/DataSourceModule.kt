package com.example.imagegallery.di

import android.content.Context
import com.example.imagegallery.data.source.local.TokenLocalDataSource
import com.example.imagegallery.data.source.local.TokenLocalDataSourceImpl
import com.example.imagegallery.data.source.local.SecureTokenManager
import com.example.imagegallery.data.source.remote.auth.AuthApi
import com.example.imagegallery.data.source.remote.auth.TokenRemoteDataSource
import com.example.imagegallery.data.source.remote.auth.TokenRemoteDataSourceImpl
import com.example.imagegallery.data.source.remote.image.ImageApi
import com.example.imagegallery.data.source.remote.image.ImageDataSource
import com.example.imagegallery.data.source.remote.image.ImageDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideTokenRemoteDataSource(authApi: AuthApi): TokenRemoteDataSource {
        return TokenRemoteDataSourceImpl(authApi = authApi)
    }

    @Provides
    @Singleton
    fun provideTokenLocalDataSource(secureTokenManager: SecureTokenManager): TokenLocalDataSource {
        return TokenLocalDataSourceImpl(secureTokenManager = secureTokenManager)
    }

    @Provides
    @Singleton
    fun provideSecureTokenManager(@ApplicationContext context: Context): SecureTokenManager {
        return SecureTokenManager(context = context)
    }

    @Provides
    @Singleton
    fun provideImageDataSource(imageApi: ImageApi): ImageDataSource {
        return ImageDataSourceImpl(imageApi = imageApi)
    }
}
