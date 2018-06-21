package com.alex.tur.driver.datamanager.location

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.alex.tur.model.DriverTransportMode
import io.reactivex.Completable

interface DriverLocationDataManager {
    fun updateCurrentLocation(location: Location, transportMode: DriverTransportMode): Completable

    fun getCurrentLocation(): MutableLiveData<Location>
}