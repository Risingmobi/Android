package com.alex.tur.driver.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.base.BaseViewModel
import com.alex.tur.driver.datamanager.order.OrderDataManager
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val profileDataManager: DriverProfileDataManager,
        private val orderDataManager: OrderDataManager
): BaseViewModel() {

    val transportModeAction = SingleLiveEvent<Result<DriverTransportMode>>()

    private val profileTrigger = SingleLiveEvent<Boolean>()
    private val activeOrderTrigger = SingleLiveEvent<Boolean>()
    private val pendingOrderListTrigger = SingleLiveEvent<Boolean>()

    val status = profileDataManager.getStatus()
    val transportMode = profileDataManager.getTransportMode()

    val profile: LiveData<Result<Driver>> = Transformations.switchMap(profileTrigger, {
        profileDataManager.getProfile(it)
    })
    val activeOrder: LiveData<Result<Order>> = Transformations.switchMap(activeOrderTrigger, {
        orderDataManager.getActiveOrder(it)
    })
    val pendingOrderList: LiveData<Result<MutableList<Order>>> = Transformations.switchMap(pendingOrderListTrigger, {
        orderDataManager.getPendingOrderList(it)
    })



    fun requestProfile(isForce: Boolean = false) {
        profileTrigger.value = isForce
    }

    fun requestActiveOrder(isForce: Boolean = false) {
        activeOrderTrigger.value = isForce
    }

    fun requestPendingOrderList(isForce: Boolean = false) {
        pendingOrderListTrigger.value = isForce
    }

    fun onTransportModeClicked() {
        transportMode.value?.let {
            transportModeAction.value = it
        }
    }

    fun changeTransportMode(newDriverTransportMode: DriverTransportMode) {
        transportMode.value?.data?.let {
            profileDataManager.changeTransportMode(newDriverTransportMode, it)
        }
    }

    fun changeStatus(newStatus: DriverStatus) {
        status.value?.data?.let {
            profileDataManager.changeStatus(newStatus, it)
        }
    }
}