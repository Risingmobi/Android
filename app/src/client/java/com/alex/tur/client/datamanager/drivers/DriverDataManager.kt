package com.alex.tur.client.datamanager.drivers

import com.alex.tur.model.Driver
import io.reactivex.Single

interface DriverDataManager {
    fun loadDrivers(swLatitude: Double, swLongitude: Double, neLatitude: Double, neLongitude: Double): Single<MutableList<Driver>>
}