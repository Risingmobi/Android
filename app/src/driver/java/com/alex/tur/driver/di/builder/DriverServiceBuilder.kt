package com.alex.tur.driver.di.builder

import com.alex.tur.di.scope.ScopeService
import com.alex.tur.driver.service.account.DriverAccountServiceModule
import com.alex.tur.service.AccountService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DriverServiceBuilder {

    @ScopeService
    @ContributesAndroidInjector(modules = [DriverAccountServiceModule::class])
    internal abstract fun bindAccountService(): AccountService

}