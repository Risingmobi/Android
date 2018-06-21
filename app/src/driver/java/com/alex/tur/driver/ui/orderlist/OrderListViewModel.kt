package com.alex.tur.driver.ui.orderlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.location.Location
import com.alex.tur.base.BaseViewModel
import com.alex.tur.driver.datamanager.location.DriverLocationDataManager
import com.alex.tur.driver.datamanager.order.OrderDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.DeleteOrder
import com.alex.tur.model.Order
import timber.log.Timber
import javax.inject.Inject

class OrderListViewModel @Inject constructor(
        private val orderDataManager: OrderDataManager,
        private val driverLocationDataManager: DriverLocationDataManager
): BaseViewModel() {

    private val activeOrderTrigger = SingleLiveEvent<Boolean>()
    private val pendingOrderListTrigger = SingleLiveEvent<Boolean>()
    private val closeOrderTrigger = SingleLiveEvent<Order>()
    private val deleteOrderTrigger = SingleLiveEvent<DeleteOrder>()

    val showReasonDialog = SingleLiveEvent<Order>()

    val activeOrder: LiveData<Result<Order>> = Transformations.switchMap(activeOrderTrigger, {
        orderDataManager.getActiveOrder(it)
    })

    val pendingOrderList: LiveData<Result<MutableList<Order>>> = Transformations.switchMap(pendingOrderListTrigger, {
        orderDataManager.getPendingOrderList(it)
    })
    val currentLocation: LiveData<Location> = driverLocationDataManager.getCurrentLocation()

    val closeActiveOrderResult: LiveData<Result<Unit>> = Transformations.switchMap(closeOrderTrigger, {
        orderDataManager.closeOrder(it)
    })
    val deleteOrderResult: LiveData<Result<Int>> = Transformations.switchMap(deleteOrderTrigger, {
        orderDataManager.deleteOrder(it)
    })


    fun requestActiveOrder(isForce: Boolean = false) {
        activeOrderTrigger.value = isForce
    }

    fun requestPendingOrderList(isForce: Boolean = false) {
        pendingOrderListTrigger.value = isForce
    }

    fun completeOrder(order: Order) {
        closeOrderTrigger.value = order
    }

    fun onDeleteOrderClicked(order: Order) {
        showReasonDialog.value = order
    }

    fun deleteOrder(orderId: Int, reason: String) {
        deleteOrderTrigger.value = DeleteOrder(reason, orderId)
    }
}