package com.alex.tur.di.module.app

import android.app.Application
import android.content.Context
import com.alex.tur.data.pref.PrefManager
import com.alex.tur.data.pref.PrefManagerImpl
import com.alex.tur.di.qualifier.global.QualifierAppContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AbsAppModule {

    @Provides
    @Singleton
    @QualifierAppContext
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @Singleton
    fun providePrefsManager(prefsManager: PrefManagerImpl): PrefManager = prefsManager
}