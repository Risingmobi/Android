package com.alex.tur.client.di.module.app

import com.alex.tur.client.datamanager.auth.AuthDataManager
import com.alex.tur.client.datamanager.auth.AuthDataManagerImpl
import com.alex.tur.client.datamanager.template.TemplateDataManager
import com.alex.tur.client.datamanager.template.TemplateDataManagerImpl
import com.alex.tur.client.datamanager.drivers.DriverDataManager
import com.alex.tur.client.datamanager.drivers.DriverDataManagerImpl
import com.alex.tur.client.datamanager.order.OrderDataManager
import com.alex.tur.client.datamanager.order.OrderDataManagerImpl
import com.alex.tur.client.datamanager.profile.ProfileDataManager
import com.alex.tur.client.datamanager.profile.ProfileDataManagerImpl
import com.alex.tur.client.datamanager.services.ServicesDataManager
import com.alex.tur.client.datamanager.services.ServicesDataManagerImpl
import com.alex.tur.client.di.module.net.NetModule
import com.alex.tur.di.module.app.AbsAppModule
import com.alex.tur.di.module.app.account.AccountModule
import com.alex.tur.di.module.app.db.DataBaseModule
import com.alex.tur.di.module.app.pref.PrefModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DataBaseModule::class, NetModule::class, PrefModule::class, AccountModule::class])
class ClientAppModule: AbsAppModule() {

    @Provides
    @Singleton
    fun authDataManager(dataManager: AuthDataManagerImpl): AuthDataManager = dataManager

    @Provides
    @Singleton
    fun profileDataManager(dataManager: ProfileDataManagerImpl): ProfileDataManager = dataManager

    @Provides
    @Singleton
    fun driversDataManager(dataManager: DriverDataManagerImpl): DriverDataManager = dataManager

    @Provides
    @Singleton
    fun servicesDataManager(dataManager: ServicesDataManagerImpl): ServicesDataManager = dataManager

    @Provides
    @Singleton
    fun orderDataManager(dataManager: OrderDataManagerImpl): OrderDataManager = dataManager

    @Provides
    @Singleton
    fun templateDataManager(dataManager: TemplateDataManagerImpl): TemplateDataManager = dataManager
}