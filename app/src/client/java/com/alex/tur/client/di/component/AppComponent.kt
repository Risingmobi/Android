package com.alex.tur.client.di.component

import android.app.Application
import com.alex.tur.client.di.builder.ClientActivityBuilder
import com.alex.tur.client.di.builder.ClientServiceBuilder
import com.alex.tur.client.di.module.app.ClientAppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ClientAppModule::class,
    ClientActivityBuilder::class,
    ClientServiceBuilder::class
])
interface AppComponent: AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}