package com.alex.tur.client.ui.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.order.OrderDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Order
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
        private val orderDataManager: OrderDataManager
): BaseViewModel() {

    private val historyTrigger = SingleLiveEvent<Boolean>()
    val history: LiveData<Result<MutableList<Order>>> = Transformations.switchMap(historyTrigger, {
        orderDataManager.loadHistory(it)
    })

    override fun onCreateView() {
        historyTrigger.value = false
    }

    fun onRefresh(isForce: Boolean) {
        historyTrigger.value = isForce
    }
}