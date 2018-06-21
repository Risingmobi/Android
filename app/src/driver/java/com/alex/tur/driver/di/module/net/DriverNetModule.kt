package com.alex.tur.driver.di.module.net

import com.alex.tur.di.module.app.net.AbsNetModule
import com.alex.tur.driver.repo.auth.DriverAuthApiRepository
import com.alex.tur.driver.repo.location.DriverLocationApiRepository
import com.alex.tur.driver.repo.order.OrderApiRepository
import com.alex.tur.driver.repo.profile.DriverProfileApiRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class DriverNetModule: AbsNetModule() {

    @Provides
    @Singleton
    fun authRepository(retrofit: Retrofit) = retrofit.create(DriverAuthApiRepository::class.java)!!

    @Provides
    @Singleton
    fun profileRepository(retrofit: Retrofit) = retrofit.create(DriverProfileApiRepository::class.java)!!

    @Provides
    @Singleton
    fun locationRepository(retrofit: Retrofit) = retrofit.create(DriverLocationApiRepository::class.java)!!

    @Provides
    @Singleton
    fun orderRepository(retrofit: Retrofit) = retrofit.create(OrderApiRepository::class.java)!!
}