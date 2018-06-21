package com.alex.tur.client.di.module.net

import com.alex.tur.client.repo.auth.AuthApiRepository
import com.alex.tur.client.repo.drivers.DriverApiRepository
import com.alex.tur.client.repo.order.OrderApiRepository
import com.alex.tur.client.repo.profile.ClientProfileApiRepository
import com.alex.tur.client.repo.services.ServicesApiRepository
import com.alex.tur.client.repo.template.TemplateApiRepository
import com.alex.tur.client.repo.template.TemplateApiRepositoryMock
import com.alex.tur.di.module.app.net.AbsNetModule
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NetModule: AbsNetModule() {

    @Provides
    @Singleton
    fun authApiRepository(retrofit: Retrofit) = retrofit.create(AuthApiRepository::class.java) as AuthApiRepository

    @Provides
    @Singleton
    fun profileApiRepository(retrofit: Retrofit) = retrofit.create(ClientProfileApiRepository::class.java) as ClientProfileApiRepository

    @Provides
    @Singleton
    fun driverApiRepository(retrofit: Retrofit) = retrofit.create(DriverApiRepository::class.java) as DriverApiRepository

    @Provides
    @Singleton
    fun servicesApiRepository(retrofit: Retrofit) = retrofit.create(ServicesApiRepository::class.java) as ServicesApiRepository

    @Provides
    @Singleton
    fun orderApiRepository(retrofit: Retrofit) = retrofit.create(OrderApiRepository::class.java) as OrderApiRepository

    @Provides
    @Singleton
    fun templateApiRepository(retrofit: Retrofit) = retrofit.create(TemplateApiRepository::class.java) as TemplateApiRepository

//    @Provides
//    @Singleton
//    fun templateApiRepository(): TemplateApiRepository {
//        return TemplateApiRepositoryMock()
//    }
}