package com.alex.tur.driver.di.component

import android.app.Application
import com.alex.tur.driver.di.builder.BroadcastReceiverBuilder
import com.alex.tur.driver.di.builder.DriverActivityBuilder
import com.alex.tur.driver.di.builder.DriverServiceBuilder
import com.alex.tur.driver.di.module.app.DriverAppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    DriverAppModule::class,
    DriverActivityBuilder::class,
    DriverServiceBuilder::class,
    BroadcastReceiverBuilder::class
])
interface AppComponent: AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}