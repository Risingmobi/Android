package com.alex.tur.client.datamanager.order

import android.arch.core.util.Function
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.client.repo.order.OrderApiRepository
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.data.db.MyDatabase
import com.alex.tur.data.pref.PrefManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleActionHandler
import com.alex.tur.helper.SingleDataLoadingHandler
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Order
import com.alex.tur.model.RequestService
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class OrderDataManagerImpl @Inject constructor(
        private val orderApiRepository: OrderApiRepository,
        private val myAccountManager: MyAccountManager,
        private val dataBase: MyDatabase,
        private val schedulerProvider: SchedulerProvider,
        private val prefsManager: PrefManager
): OrderDataManager {

    var lastUpdatedTimeForPending = 0L

    private val ordersLoader = object : SingleDataLoadingHandler<MutableList<Order>>(schedulerProvider, "ORDERS") {
        override fun loadFromDb(params: Map<String, String>?): Single<MutableList<Order>>? {
            return Single.fromCallable {dataBase.orderDao().findAllOrders()}
        }

        override fun loadFromNet(params: Map<String, String>?): Single<MutableList<Order>> {
            return orderApiRepository.getOrdersInProgress(myAccountManager.getAuthHeader())
        }

        override fun saveFromNet(data: MutableList<Order>) {
            lastUpdatedTimeForPending = System.currentTimeMillis()
            dataBase.orderDao().removeAll()
            dataBase.orderDao().insertOrders(data)
        }
    }

    private val historyLoader = object : SingleDataLoadingHandler<MutableList<Order>>(schedulerProvider, "HISTORY") {
        override fun loadFromDb(params: Map<String, String>?): Single<MutableList<Order>>? {
            return null
        }

        override fun loadFromNet(params: Map<String, String>?): Single<MutableList<Order>> {
            return orderApiRepository.getHistory(myAccountManager.getAuthHeader())
        }

        override fun saveFromNet(data: MutableList<Order>) {

        }
    }


    private val validatePaymentHandler = object : SingleActionHandler<RequestService, Order>(schedulerProvider, "VALIDATE_PAYMENT") {

        override fun performAction(requestData: RequestService): Single<Order> {
            var picturePart: MultipartBody.Part? = null
            requestData.requestDescription?.picture?.also { picture ->
                val file = File(requestData.requestDescription?.picture)
                Timber.d("picture size ${file.length()}")
                val pictureRequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                picturePart = MultipartBody.Part.createFormData("request_descr_picture", file.name, pictureRequestBody)
            }

            val commentPart = requestData.requestDescription?.brieflyDescription?.let {
                RequestBody.create(MediaType.parse("text/plain"), it)
            }

            val latPart = RequestBody.create(MediaType.parse("text/plain"), requestData.lat.toString())
            val lngPart = RequestBody.create(MediaType.parse("text/plain"), requestData.lng.toString())
            val serviceIdPart = RequestBody.create(MediaType.parse("text/plain"), requestData.service?.id.toString())

            return orderApiRepository.requestService(
                    myAccountManager.getAuthHeader(),
                    picturePart,
                    latPart,
                    lngPart,
                    commentPart,
                    serviceIdPart)
        }
    }

    override fun validatePayment(requestService: RequestService): LiveData<Result<Order>> {
        return validatePaymentHandler.execute(requestService)
    }

    override fun loadOrders(isForce: Boolean): LiveData<Result<MutableList<Order> >> {
        return if (isForce) {
            ordersLoader.execute(true)
        } else {
            ordersLoader.execute((lastUpdatedTimeForPending == 0L || (System.currentTimeMillis() - lastUpdatedTimeForPending) > 30000))
        }
    }

    override fun loadHistory(isForce: Boolean): LiveData<Result<MutableList<Order> >> {
        return historyLoader.execute(isForce)
    }


    val trigger = SingleLiveEvent<Boolean>()
    val trackingOrderTrigger: LiveData<Result<MutableList<Order>>> = Transformations.switchMap(trigger, {
        loadOrders(it)
    })

    private val trackingOrder = Transformations.map(trackingOrderTrigger, object : Function<Result<MutableList<Order>>, Result<Order?>> {
        override fun apply(it: Result<MutableList<Order>>): Result<Order?> {
            val trackingOrderId = prefsManager.getTrackingOrderId()
            Timber.d("map ${it.type}, ${it.status}")
            Timber.tag("SingleLoader").d("getTrackingOrder map %s, %s, %s", it?.status, it?.type, trackingOrderId)
            it.data?.also { list ->
                for (order in list) {
                    if (trackingOrderId == order.id) {
                        when(it.status) {
                            Result.Status.SUCCESS -> {
                                when(it.type) {
                                    Result.Type.LOCAL -> return Result.successFromLocal(order)
                                    Result.Type.REMOTE -> return Result.successFromRemote(order)
                                }
                            }
                            Result.Status.ERROR -> {
                                when(it.type) {
                                    Result.Type.LOCAL -> return Result.errorFromLocal(it.message, order)
                                    Result.Type.REMOTE -> return Result.errorFromRemote(it.message, order)
                                }
                            }
                            Result.Status.LOADING -> {
                                when(it.type) {
                                    Result.Type.LOCAL -> return Result.loading(order)
                                    Result.Type.REMOTE -> return Result.loading(order)
                                }
                            }
                        }
                    }
                }

                if (list.isNotEmpty()) {
                    when(it.status) {
                        Result.Status.SUCCESS -> {
                            when(it.type) {
                                Result.Type.LOCAL -> return Result.successFromLocal(list[0])
                                Result.Type.REMOTE -> return Result.successFromRemote(list[0])
                            }
                        }
                        Result.Status.ERROR -> {
                            when(it.type) {
                                Result.Type.LOCAL -> return Result.errorFromLocal(it.message, list[0])
                                Result.Type.REMOTE -> return Result.errorFromRemote(it.message, list[0])
                            }
                        }
                        Result.Status.LOADING -> {
                            when(it.type) {
                                Result.Type.LOCAL -> return Result.loading(list[0])
                                Result.Type.REMOTE -> return Result.loading(list[0])
                            }
                        }
                    }
                }
            }
            return Result.successFromRemote(null)
        }
    })

    override fun getTrackingOrder(isForce: Boolean): LiveData<Result<Order?>> {
        Timber.w("getTrackingOrder")
        trigger.value = isForce
        return trackingOrder
    }



    override fun saveTrackingOrderId(id: Int?) {
        prefsManager.setTrackingOrderId(id)
    }
}