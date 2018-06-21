package com.alex.tur.di.module.app.pref

import android.content.Context
import android.content.SharedPreferences
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.di.qualifier.global.QualifierPrefName
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrefModule {

    @Provides
    @Singleton
    @QualifierPrefName
    fun providePrefName() = "tur.pref"

    @Provides
    @Singleton
    fun provideSharedPrefs(@QualifierAppContext appContext: Context, @QualifierPrefName name: String) : SharedPreferences {
        return appContext.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}