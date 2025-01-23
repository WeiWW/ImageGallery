package com.example.imagegallery.di

import com.example.imagegallery.data.repository.TokenRepository
import com.example.imagegallery.data.repository.TokenRepositoryImpl
import com.example.imagegallery.data.source.local.TokenLocalDataSource
import com.example.imagegallery.data.source.remote.auth.TokenRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideTokenRepository(
        tokenLocalDataSource: TokenLocalDataSource,
        tokenRemoteDataSource: TokenRemoteDataSource,
        ioDispatcher: CoroutineDispatcher
    ): TokenRepository {
        return TokenRepositoryImpl(tokenRemoteDataSource, tokenLocalDataSource, ioDispatcher)
    }
}