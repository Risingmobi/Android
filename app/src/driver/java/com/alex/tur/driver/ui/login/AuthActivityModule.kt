package com.alex.tur.driver.ui.login

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.di.scope.ScopeFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthActivityModule {

    @get:ScopeFragment
    @get:ContributesAndroidInjector
    internal abstract val loginFragment: LoginFragment
}
