package com.alex.tur.client.ui.login

import com.alex.tur.di.scope.ScopeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthActivityModule {

    @get:ScopeFragment
    @get:ContributesAndroidInjector
    internal abstract val loginFragment: LoginFragment

    @get:ScopeFragment
    @get:ContributesAndroidInjector
    internal abstract val signupFragment: SignUpFragment
}