package com.alex.tur.driver.datamanager.profile

import android.arch.lifecycle.LiveData
import com.alex.tur.helper.Result
import com.alex.tur.model.Driver
import com.alex.tur.model.Service
import com.alex.tur.model.DriverStatus
import com.alex.tur.model.DriverTransportMode
import io.reactivex.Completable
import io.reactivex.Single

interface DriverProfileDataManager {
    fun getProfile(isForce: Boolean): LiveData<Result<Driver>>
    fun changeStatus(newStatus: DriverStatus, currentStatus: DriverStatus)
    fun logOut(): Completable
    fun changePassword(password: String): Completable
    fun getServices(isForce: Boolean): LiveData<Result<MutableList<Service>>>
    fun changeTransportMode(newDriverTransportMode: DriverTransportMode, currentDriverTransportMode: DriverTransportMode)

    fun getStatus(): LiveData<Result<DriverStatus>>
    fun getTransportMode(): LiveData<Result<DriverTransportMode>>
    fun changeServices(checkedServices: MutableList<Service>): Completable
    fun changeCar(driver: Driver): LiveData<Result<Driver>>
}