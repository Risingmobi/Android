package com.alex.tur.client.ui.orders

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.order.OrderDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Order
import com.alex.tur.model.OrderDescription
import javax.inject.Inject

class OrdersViewModel @Inject constructor(
        private val orderDataManager: OrderDataManager
): BaseViewModel() {

    private val orderListTrigger = SingleLiveEvent<Boolean>()
    private val trackingOrderTrigger = SingleLiveEvent<Unit>()

    val goToMapAction = SingleLiveEvent<Unit>()
    val openHistoryAction = SingleLiveEvent<Unit>()
    val openCommentAction = SingleLiveEvent<OrderDescription>()

    val orderListResult: LiveData<Result<MutableList<Order>>> = Transformations.switchMap(orderListTrigger, {
        orderDataManager.loadOrders(it)
    })

    fun requestOrders(isForce: Boolean = false) {
        orderListTrigger.value = isForce
    }

    private fun requestTrackingOrder() {
        trackingOrderTrigger.call()
    }

    fun onItemClicked(order: Order) {
        orderDataManager.saveTrackingOrderId(order.id)
        requestTrackingOrder()
        goToMapAction.call()
    }

    fun onAttachmentClicked(comment: OrderDescription) {
        openCommentAction.value = comment
    }

    fun onHistoryClicked() {
        openHistoryAction.call()
    }
}