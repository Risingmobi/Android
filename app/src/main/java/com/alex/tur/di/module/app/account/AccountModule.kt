package com.alex.tur.di.module.app.account

import android.content.Context
import com.alex.tur.R
import com.alex.tur.di.qualifier.global.QualifierAccountType
import com.alex.tur.di.qualifier.global.QualifierAppContext
import dagger.Module
import dagger.Provides

@Module
class AccountModule {

//    @Provides
//    @Singleton
//    fun provideMyAccountManager()

    @Provides
    @QualifierAccountType
    fun provideAccountType(@QualifierAppContext context: Context) = context.getString(R.string.account_type)!!
}