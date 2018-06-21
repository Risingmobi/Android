package com.alex.tur.driver.ui.main

import com.alex.tur.di.scope.ScopeFragment
import com.alex.tur.driver.ui.map.FragmentMapModule
import com.alex.tur.driver.ui.map.MapFragment
import com.alex.tur.driver.ui.orderlist.OrderListFragment
import com.alex.tur.driver.ui.profile.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ScopeFragment
    @ContributesAndroidInjector(modules = [FragmentMapModule::class])
    abstract fun provideMapFragmentFactory(): MapFragment

    @ScopeFragment
    @ContributesAndroidInjector()
    abstract fun provideProfileFragmentFactory(): ProfileFragment

    @ScopeFragment
    @ContributesAndroidInjector()
    abstract fun provideOrderListFragmentFactory(): OrderListFragment
}