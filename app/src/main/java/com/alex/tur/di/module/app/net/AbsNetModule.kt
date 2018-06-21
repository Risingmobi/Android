package com.alex.tur.di.module.app.net

import com.alex.tur.AppConstants
import com.alex.tur.data.auth.OkHttpAuthenticator
import com.alex.tur.data.net.EnumRetrofitConverterFactory
import com.alex.tur.di.qualifier.global.QualifierOkHttpAppInterceptors
import com.alex.tur.di.qualifier.global.QualifierOkHttpNetworkInterceptors
import com.alex.tur.scheduler.AppSchedulerProvider
import com.alex.tur.scheduler.SchedulerProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [AppInterceptorsModule::class, NetworkInterceptorsModule::class])
abstract class AbsNetModule {

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

    @Provides
    @Singleton
    fun provideRxAdapterFactory(schedulerProvider: SchedulerProvider)
//            = RxJava2CallAdapterFactory.createWithScheduler(schedulerProvider.io())
            = RxJava2CallAdapterFactory.createAsync()

    @Provides
    @Singleton
    fun provideConverterFactory(gson: Gson) = GsonConverterFactory.create(gson)!!

    @Provides
    @Singleton
    fun provideEnumConverterFactory() = EnumRetrofitConverterFactory()

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder().create()!!

    @Provides
    @Singleton
    fun provideOkHttpClient(@QualifierOkHttpNetworkInterceptors netInterceptors: Set<@JvmSuppressWildcards Interceptor>,
                            @QualifierOkHttpAppInterceptors appInterceptors: Set<@JvmSuppressWildcards Interceptor>,
                            okHttpAuthenticator: OkHttpAuthenticator): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()

        for (interceptor in appInterceptors) {
            okHttpBuilder.addInterceptor(interceptor)
        }
        for (networkInterceptor in netInterceptors) {
            okHttpBuilder.addNetworkInterceptor(networkInterceptor)
        }

        okHttpBuilder.authenticator(okHttpAuthenticator)
        okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(60, TimeUnit.SECONDS)

        return okHttpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(converterFactory : GsonConverterFactory,
                        rxAdapter: RxJava2CallAdapterFactory,
                        okHttpClient: OkHttpClient) = Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(rxAdapter)
            .client(okHttpClient)
            .build()!!
}