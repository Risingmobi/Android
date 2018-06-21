package com.alex.tur.driver.di.builder

import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.driver.ui.login.AuthActivityModule
import com.alex.tur.driver.ui.login.AuthActivity
import com.alex.tur.driver.ui.main.MainActivity
import com.alex.tur.driver.ui.main.MainActivityModule
import com.alex.tur.driver.ui.profile.edit.DriverEditActivity
import com.alex.tur.driver.ui.profile.edit.DriverEditActivityModule
import com.alex.tur.driver.ui.profile.edit_car.EditCarActivity
import com.alex.tur.driver.ui.profile.edit_car.EditCarActivityModule
import com.alex.tur.driver.ui.servicelist.DriverServicesActivity
import com.alex.tur.driver.ui.servicelist.DriverServicesActivityModule
import com.alex.tur.driver.ui.splash.DriverSplashActivity
import com.alex.tur.driver.ui.splash.SplashActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DriverActivityBuilder {

    @ScopeActivity
    @ContributesAndroidInjector(modules = [AuthActivityModule::class])
    internal abstract fun bindLoginActivity(): AuthActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [SplashActivityModule::class])
    internal abstract fun bindSplashActivity(): DriverSplashActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [DriverEditActivityModule::class])
    internal abstract fun bindEditActivity(): DriverEditActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [DriverServicesActivityModule::class])
    internal abstract fun bindDriverServicesActivity(): DriverServicesActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [EditCarActivityModule::class])
    internal abstract fun bindEditCarActivity(): EditCarActivity
}