package com.alex.tur.driver.datamanager.order

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.data.db.MyDatabase
import com.alex.tur.driver.repo.order.OrderApiRepository
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleActionHandler
import com.alex.tur.helper.SingleDataLoadingHandler
import com.alex.tur.model.DeleteOrder
import com.alex.tur.model.Order
import com.alex.tur.model.OrderStatus
import com.alex.tur.model.api.RequestDeleteOrder
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class OrderDataManagerImpl @Inject constructor(
        private val orderApiRepository: OrderApiRepository,
        private val myAccountManager: MyAccountManager,
        private val schedulerProvider: SchedulerProvider,
        private val dataBase: MyDatabase
): OrderDataManager {

    private val activeOrderLoader = object : SingleDataLoadingHandler<Order>(schedulerProvider, "ACTIVE_ORDER") {

        override fun loadFromDb(params: Map<String, String>?): Single<Order>? {
            return Single.fromCallable {
                dataBase.orderDao().findOrderByStatus(OrderStatus.ACTIVE.name)
            }
        }

        override fun loadFromNet(params: Map<String, String>?): Single<Order> {
            Timber.d("loadFromNet")
            return orderApiRepository.getActiveOrder(myAccountManager.getAuthHeader())
                    .map {
                        Timber.d("loadFromNet %s", it)
                        if (it.id == null) {
                            null
                        } else {
                            it
                        }
                    }
        }

        override fun saveFromNet(data: Order) {
            dataBase.orderDao().removeAllByStatus(OrderStatus.ACTIVE.name)
            dataBase.orderDao().insertOrder(data)
        }
    }

    private val pendingOrderListLoader = object : SingleDataLoadingHandler<MutableList<Order>>(schedulerProvider, "PENDING_ORDER_LIST") {

        override fun loadFromDb(params: Map<String, String>?): Single<MutableList<Order>>? {
            return Single.fromCallable {dataBase.orderDao().findOrderListByStatus(OrderStatus.PENDING.name)}
        }

        override fun loadFromNet(params: Map<String, String>?): Single<MutableList<Order>> {
            return orderApiRepository.getPendingOrders(myAccountManager.getAuthHeader())
        }

        override fun saveFromNet(data: MutableList<Order>) {
            dataBase.orderDao().removeAllByStatus(OrderStatus.PENDING.name)
            dataBase.orderDao().insertOrders(data)
        }
    }



    private val closeActiveOrderLoader = object : SingleActionHandler<Unit, Unit>(schedulerProvider, "CLOSE_ACTIVE_ORDER") {

        override fun performAction(requestData: Unit): Single<Unit> {
            val order = Order()
            order.status = OrderStatus.CLOSE
            return orderApiRepository.cloceActiveOrder(myAccountManager.getAuthHeader(), order)
        }

    }

    private val deleteActiveOrderLoader = object : SingleActionHandler<DeleteOrder, Int>(schedulerProvider, "DELETE_ORDER") {
        override fun performAction(requestData: DeleteOrder): Single<Int> {
            return orderApiRepository.deleteOrder(myAccountManager.getAuthHeader(), requestData.orderId, RequestDeleteOrder(requestData.reason))
                    .map { requestData.orderId }
        }
    }



    override fun getActiveOrder(isForce: Boolean): LiveData<Result<Order>> {
        return activeOrderLoader.execute(isForce)
    }

    override fun getPendingOrderList(isForce: Boolean): LiveData<Result<MutableList<Order>>> {
        return pendingOrderListLoader.execute(isForce)
    }

    override fun closeOrder(order: Order): LiveData<Result<Unit>> {
        return closeActiveOrderLoader.execute(Unit)
    }

    override fun deleteOrder(deleteOrder: DeleteOrder): LiveData<Result<Int>> {
        return deleteActiveOrderLoader.execute(deleteOrder)
    }
}