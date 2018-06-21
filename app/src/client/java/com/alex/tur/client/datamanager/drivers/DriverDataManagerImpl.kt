package com.alex.tur.client.datamanager.drivers

import com.alex.tur.client.repo.drivers.DriverApiRepository
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.model.Driver
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class DriverDataManagerImpl @Inject constructor(
        private val driversApiRepository: DriverApiRepository,
        private val myAccountManager: MyAccountManager,
        private val schedulerProvider: SchedulerProvider
): DriverDataManager {

    override fun loadDrivers(swLatitude: Double, swLongitude: Double, neLatitude: Double, neLongitude: Double): Single<MutableList<Driver>> {
        return driversApiRepository.getDrivers(myAccountManager.getAuthHeader(), swLatitude, swLongitude, neLatitude, neLongitude)
                .observeOn(schedulerProvider.ui())
    }
}