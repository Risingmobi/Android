package com.alex.tur.driver.di.module.app

import com.alex.tur.di.module.app.AbsAppModule
import com.alex.tur.di.module.app.account.AccountModule
import com.alex.tur.di.module.app.db.DataBaseModule
import com.alex.tur.di.module.app.pref.PrefModule
import com.alex.tur.driver.di.module.net.DriverNetModule
import com.alex.tur.driver.datamanager.auth.DriverAuthDataManager
import com.alex.tur.driver.datamanager.auth.AuthDataManagerImpl
import com.alex.tur.driver.datamanager.location.DriverLocationDataManager
import com.alex.tur.driver.datamanager.location.DriverLocationDataManagerImpl
import com.alex.tur.driver.datamanager.order.OrderDataManager
import com.alex.tur.driver.datamanager.order.OrderDataManagerImpl
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManager
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DataBaseModule::class, DriverNetModule::class, PrefModule::class, AccountModule::class])
class DriverAppModule: AbsAppModule() {

    @Provides
    @Singleton
    fun authDataManager(dataManager: AuthDataManagerImpl): DriverAuthDataManager = dataManager

    @Provides
    @Singleton
    fun profileDataManager(dataManager: DriverProfileDataManagerImpl): DriverProfileDataManager = dataManager

    @Provides
    @Singleton
    fun locationDataManager(dataManager: DriverLocationDataManagerImpl): DriverLocationDataManager = dataManager

    @Provides
    @Singleton
    fun orderDataManager(dataManager: OrderDataManagerImpl): OrderDataManager = dataManager

}