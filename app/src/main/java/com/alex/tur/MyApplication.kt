package com.alex.tur

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.multidex.MultiDex
import android.support.v4.app.Fragment
import android.util.Log
import com.alex.tur.model.MyAddress
import com.alex.tur.model.OrderStatus
import com.alex.tur.utils.ThreadUtils
import com.facebook.stetho.Stetho
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import dagger.android.DaggerApplication
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import timber.log.Timber
import java.net.ConnectException
import kotlin.system.measureTimeMillis

abstract class MyApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        setupRxErrorHandler()
        initTimber()
        initStetho()

        if (BuildConfig.DEBUG) {
            FirebaseApp.initializeApp(this)
        }
    }

    private fun setupRxErrorHandler() {
        RxJavaPlugins.setErrorHandler { t -> Timber.e(t, "My uncaught error") }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                        return
                    }
                    if (t != null && t !is ConnectException) {
                        try {
//                            Crashlytics.logException(t)
//                            FirebaseCrash.report(t)
                        } catch (ignore: Exception) {
                            //ignore
                        }
                    }
                }
            })
        }
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
