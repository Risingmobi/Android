package com.alex.tur.base

import android.arch.lifecycle.LifecycleService
import dagger.android.AndroidInjection

abstract class BaseService: LifecycleService() {


    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

}