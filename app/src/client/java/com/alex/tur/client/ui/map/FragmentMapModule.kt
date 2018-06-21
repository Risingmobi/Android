package com.alex.tur.client.ui.map

import com.alex.tur.base.MapManager
import com.alex.tur.client.datamanager.MapRouteManager
import dagger.Module
import dagger.Provides

@Module
abstract class FragmentMapModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun mapManager(fragment: MapFragment): MapManager {
            val mapManager = MapManager(fragment.activity!!, fragment.viewLifecycleOwner)
            fragment.getViewLifeCycle().addObserver(mapManager)
            mapManager.setCallback(fragment)
            return mapManager
        }

        @Provides
        @JvmStatic
        fun mapRouteManager(fragment: MapFragment): MapRouteManager {
            val mapManager = MapRouteManager(fragment.activity!!, fragment.viewLifecycleOwner)
            fragment.getViewLifeCycle().addObserver(mapManager)
            mapManager.setCallback(fragment)
            return mapManager
        }
    }
}