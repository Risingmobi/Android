package com.alex.tur.client

import com.alex.tur.MyApplication
import com.alex.tur.client.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class ClientApplication: MyApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .application(this)
                .build()
    }
}