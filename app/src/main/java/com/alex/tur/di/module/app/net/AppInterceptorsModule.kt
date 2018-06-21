package com.alex.tur.di.module.app.net

import com.alex.tur.di.qualifier.global.QualifierOkHttpAppInterceptors
import com.alex.tur.interceptor.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
class AppInterceptorsModule {

    @IntoSet
    @Provides
    @Singleton
    @QualifierOkHttpAppInterceptors
    fun provideNetworkAvailableInterceptor(interceptor: ErrorInterceptor) : Interceptor = interceptor

    @IntoSet
    @Provides
    @Singleton
    @QualifierOkHttpAppInterceptors
    fun provideLoggingInterceptor(): Interceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)

}