package com.arfakhsy.pixcels_assessment.di

import com.arfakhsy.pixcels_assessment.core.analytics.AnalyticsTracker
import com.arfakhsy.pixcels_assessment.core.analytics.LogAnalyticsTracker
import com.arfakhsy.pixcels_assessment.data.datasource.NativeEncryptionDataSource
import com.arfakhsy.pixcels_assessment.data.datasource.NativeEncryptionDataSourceImpl
import com.arfakhsy.pixcels_assessment.data.repository.EncryptionRepository
import com.arfakhsy.pixcels_assessment.data.repository.EncryptionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindEncryptionRepository(
        encryptionRepositoryImpl: EncryptionRepositoryImpl
    ): EncryptionRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(
        logAnalyticsTracker: LogAnalyticsTracker
    ): AnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindNativeEncryptionDataSource(
        nativeEncryptionDataSourceImpl: NativeEncryptionDataSourceImpl
    ): NativeEncryptionDataSource

    companion object {
        @Provides
        @Singleton
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
