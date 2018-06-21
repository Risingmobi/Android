package com.alex.tur.client.di.builder

import com.alex.tur.client.service.ClientAccountServiceModule
import com.alex.tur.di.scope.ScopeService
import com.alex.tur.service.AccountService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ClientServiceBuilder {

    @ScopeService
    @ContributesAndroidInjector(modules = [ClientAccountServiceModule::class])
    internal abstract fun bindAccountService(): AccountService
}