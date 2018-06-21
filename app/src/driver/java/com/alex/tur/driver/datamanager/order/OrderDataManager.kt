package com.alex.tur.driver.datamanager.order

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.alex.tur.helper.Result
import com.alex.tur.model.DeleteOrder
import com.alex.tur.model.Order
import io.reactivex.Single

interface OrderDataManager {
    fun getPendingOrderList(isForce: Boolean): LiveData<Result<MutableList<Order>>>
    fun getActiveOrder(isForce: Boolean): LiveData<Result<Order>>
    fun closeOrder(order: Order): LiveData<Result<Unit>>
    fun deleteOrder(deleteOrder: DeleteOrder): LiveData<Result<Int>>
}