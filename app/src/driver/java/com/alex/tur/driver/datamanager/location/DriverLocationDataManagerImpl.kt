package com.alex.tur.driver.datamanager.location

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.driver.repo.location.DriverLocationApiRepository
import com.alex.tur.ext.roundToDecimalPlaces
import com.alex.tur.model.Driver
import com.alex.tur.model.DriverTransportMode
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class DriverLocationDataManagerImpl @Inject constructor(
        private val myAccountManager: MyAccountManager,
        private val schedulerProvider: SchedulerProvider,
        private val driverLocationApiRepository: DriverLocationApiRepository
): DriverLocationDataManager {

    private val currentLocationLiveData = MutableLiveData<Location>()

    override fun getCurrentLocation(): MutableLiveData<Location> {
        return currentLocationLiveData
    }

    override fun updateCurrentLocation(location: Location, transportMode: DriverTransportMode): Completable {
        currentLocationLiveData.value = location
        val driver = Driver()
        driver.lat = location.latitude.roundToDecimalPlaces(6)
        driver.lng = location.longitude.roundToDecimalPlaces(6)
        driver.transportMode = transportMode
        return driverLocationApiRepository.updateLocation(myAccountManager.getAuthHeader(), driver)
                .observeOn(schedulerProvider.ui())
    }
}