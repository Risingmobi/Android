package com.alex.tur.driver.di.builder

import com.alex.tur.di.scope.ScopeBroadcast
import com.alex.tur.driver.broadcast.DriverLocationReceiver
import com.alex.tur.driver.broadcast.LocationReceiverModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverBuilder {

    @ScopeBroadcast
    @ContributesAndroidInjector(modules = [LocationReceiverModule::class])
    internal abstract fun bindAccountService(): DriverLocationReceiver
}