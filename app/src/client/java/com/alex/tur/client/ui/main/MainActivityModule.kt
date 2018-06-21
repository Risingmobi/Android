package com.alex.tur.client.ui.main

import com.alex.tur.client.ui.map.FragmentMapModule
import com.alex.tur.client.ui.map.MapFragment
import com.alex.tur.client.ui.profile.ProfileFragment
import com.alex.tur.client.ui.orders.FragmentOrders
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector()
    abstract fun provideServicesFragmentFactory(): FragmentOrders

    @ContributesAndroidInjector()
    abstract fun provideProfileFragmentFactory(): ProfileFragment

    @ContributesAndroidInjector(modules = [FragmentMapModule::class])
    abstract fun provideMapFragmentFactory(): MapFragment

}