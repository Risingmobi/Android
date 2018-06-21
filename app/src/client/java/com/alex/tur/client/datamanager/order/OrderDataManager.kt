package com.alex.tur.client.datamanager.order

import android.arch.lifecycle.LiveData
import com.alex.tur.helper.Result
import com.alex.tur.model.Order
import com.alex.tur.model.RequestService
import com.alex.tur.model.Template

interface OrderDataManager {
    fun validatePayment(requestService: RequestService): LiveData<Result<Order>>

    fun loadOrders(isForce: Boolean): LiveData<Result<MutableList<Order>>>
    fun getTrackingOrder(isForce: Boolean): LiveData<Result<Order?>>
    fun saveTrackingOrderId(id: Int?)
    fun loadHistory(isForce: Boolean): LiveData<Result<MutableList<Order>>>
}