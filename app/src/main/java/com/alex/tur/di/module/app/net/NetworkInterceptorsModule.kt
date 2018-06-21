package com.alex.tur.di.module.app.net

import com.alex.tur.di.qualifier.global.QualifierOkHttpNetworkInterceptors
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
class NetworkInterceptorsModule {

    @IntoSet
    @Provides
    @Singleton
    @QualifierOkHttpNetworkInterceptors
    fun provideOkHttpNetworkInterceptors(): Interceptor {
        return StethoInterceptor()
    }
}