package com.alex.tur.driver

import com.alex.tur.MyApplication
import com.alex.tur.driver.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class DriverApplication: MyApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .application(this)
                .build()
    }
}