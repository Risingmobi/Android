package com.alex.tur.driver.repo.order

import com.alex.tur.model.Order
import com.alex.tur.model.api.RequestDeleteOrder
import io.reactivex.Single
import retrofit2.http.*

interface OrderApiRepository {

    @GET("driver_orders/")
    fun getAllOrders(@Header("Authorization") header: String): Single<MutableList<Order>>

    @GET("driver_active_order/")
    fun getActiveOrder(@Header("Authorization") header: String): Single<Order>

    @PATCH("driver_active_order/")
    fun cloceActiveOrder(@Header("Authorization") header: String, @Body order: Order): Single<Unit>

    @GET("driver_pending_orders/")
    fun getPendingOrders(@Header("Authorization") header: String): Single<MutableList<Order>>

    @GET("driver_order/{id}/")
    fun getOrder(@Header("Authorization") header: String, @Path("id") id: Int): Single<Order>

    @PATCH("driver_remove_order/{id}/")
    fun deleteOrder(@Header("Authorization") header: String, @Path("id") id: Int?, @Body body: RequestDeleteOrder): Single<Unit>
}