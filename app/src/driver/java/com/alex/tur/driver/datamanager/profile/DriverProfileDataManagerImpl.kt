package com.alex.tur.driver.datamanager.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.driver.repo.profile.DriverProfileApiRepository
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleActionHandler
import com.alex.tur.helper.SingleDataChangeHandler
import com.alex.tur.helper.SingleDataLoadingHandler
import com.alex.tur.model.Driver
import com.alex.tur.model.Service
import com.alex.tur.model.DriverStatus
import com.alex.tur.model.DriverTransportMode
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverProfileDataManagerImpl
@Inject constructor(
        private val profileApiRepository: DriverProfileApiRepository,
        private val myAccountManager: MyAccountManager,
        private val schedulerProvider: SchedulerProvider
): DriverProfileDataManager {

    private val profileLoader = object: SingleDataLoadingHandler<Driver>(schedulerProvider, "PROFILE") {
        override fun loadFromDb(params: Map<String, String>?): Single<Driver> {
            return myAccountManager.getDriverProfile()
        }
        override fun loadFromNet(params: Map<String, String>?): Single<Driver> {
            return profileApiRepository.getProfile(myAccountManager.getAuthHeader())
        }
        override fun saveFromNet(data: Driver) {
            myAccountManager.updateDriverProfile(data)
        }
    }

    private val servicesLoader = object: SingleDataLoadingHandler<MutableList<Service>>(schedulerProvider, "DR_SERVICES") {
        override fun loadFromDb(params: Map<String, String>?): Single<MutableList<Service>>? {
            return null //TODO
        }
        override fun loadFromNet(params: Map<String, String>?): Single<MutableList<Service>> {
            return Single.zip(
                    profileApiRepository.getProfile(myAccountManager.getAuthHeader()),
                    profileApiRepository.getServices(myAccountManager.getAuthHeader()),
                    BiFunction<Driver, MutableList<Service>, MutableList<Service>> { driver, services ->
                        for (service in services) {
                            driver.availableServices?.let {
                                for (driverService in it) {
                                    if (driverService.id == service.id) {
                                        service.isChecked = true
                                        service.isMine = true
                                    }
                                }
                            }
                        }
                        return@BiFunction services
                    })
        }
        override fun saveFromNet(data: MutableList<Service>) {
            //TODO
        }
    }

    private val changeCarActionHandler = object : SingleActionHandler<Driver, Driver>(schedulerProvider, "CHANGE_CAR") {
        override fun performAction(requestData: Driver): Single<Driver> {
            return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), requestData)
                    .map {
                        myAccountManager.changeVehicleModel(it.vehicleModel)
                        myAccountManager.changeVehicleNumber(it.vehicleNumber)
                        profileLoader.refreshDataForAllSubscribers()
                        it
                    }
        }
    }

    private val statusChanger = object: SingleDataChangeHandler<DriverStatus, Driver>(schedulerProvider, "STATUS") {
        override fun performChange(type: DriverStatus): Single<Driver> {
            val driver = Driver()
            driver.status = type
            return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), driver)
        }
        override fun saveAndGetResult(data: Driver): DriverStatus? {
            myAccountManager.changeStatus(data.status)
            return data.status
        }
    }

    private val transportModeChanger = object: SingleDataChangeHandler<DriverTransportMode, Driver>(schedulerProvider, "TRANSPORT") {
        override fun performChange(type: DriverTransportMode): Single<Driver> {
            val driver = Driver()
            driver.transportMode = type
            return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), driver)
        }
        override fun saveAndGetResult(data: Driver): DriverTransportMode? {
            myAccountManager.changeTransportMode(data.transportMode)
            return data.transportMode
        }
    }

    init {
        profileLoader.resultLiveData.addSource(statusChanger.result, {
            Timber.tag("SingleDataChangeHandler").w("statusChanger %s", it)
            it?.let {
                profileLoader.resultLiveData.value?.data?.status = it.data
            }
        })
        profileLoader.resultLiveData.addSource(transportModeChanger.result, {
            it?.let {
                profileLoader.resultLiveData.value?.data?.transportMode = it.data
            }
        })
    }

    override fun getProfile(isForce: Boolean): LiveData<Result<Driver>> {
        return profileLoader.execute(isForce)
    }

    override fun getServices(isForce: Boolean): LiveData<Result<MutableList<Service>>> {
        return servicesLoader.execute(isForce)
    }

    override fun getStatus(): LiveData<Result<DriverStatus>> {
        return Transformations.switchMap(profileLoader.resultLiveData, {
            Timber.tag("SingleDataChangeHandler").w("profileLoader %s", it)
            statusChanger.update(it.data?.status)
        })
    }

    override fun getTransportMode(): LiveData<Result<DriverTransportMode>> {
        return Transformations.switchMap(profileLoader.resultLiveData, {
            transportModeChanger.update(it.data?.transportMode)
        })
    }








    override fun changePassword(password: String): Completable {
        val driver = Driver()
        driver.password = password
        return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), driver)
                .doOnSuccess {
                    myAccountManager.changePassword(it.password)
                    profileLoader.refreshDataForAllSubscribers()
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun changeStatus(newStatus: DriverStatus, currentStatus: DriverStatus) {
        statusChanger.execute(newStatus, currentStatus)
    }

    override fun changeTransportMode(newDriverTransportMode: DriverTransportMode, currentDriverTransportMode: DriverTransportMode) {
        transportModeChanger.execute(newDriverTransportMode, currentDriverTransportMode)
    }

    override fun changeServices(checkedServices: MutableList<Service>): Completable {
        val driver = Driver()
        driver.availableServices = mutableListOf()
        for (service in checkedServices) {
            driver.availableServices?.add(Service().apply {
                id = service.id
                naming = service.naming
            })
        }
        return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), driver)
                .doOnSuccess {
                    myAccountManager.changeAvailableServices(it.availableServices)
                    profileLoader.refreshDataForAllSubscribers()
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun changeCar(driver: Driver): LiveData<Result<Driver>> {
        return changeCarActionHandler.execute(driver)
    }

    override fun logOut(): Completable {
        return Completable.fromCallable {
            myAccountManager.logout()
        }
    }
}