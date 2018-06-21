package com.alex.tur.client.repo.order

import com.alex.tur.model.Order
import com.alex.tur.model.RequestService
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OrderApiRepository {

    @Multipart
    @POST("customer_orders/")
    fun requestService(
            @Header("Authorization") header: String,
            @Part picturePart: MultipartBody.Part?,
            @Part("lat") latPart: RequestBody,
            @Part("lng") lngPart: RequestBody,
            @Part("request_descr_briefly_description") commentPart: RequestBody?,
            @Part("service_descr_id") serviceIdPart: RequestBody
    ): Single<Order>

    @GET("customer_orders/")
    fun getOrdersInProgress(@Header("Authorization") header: String): Single<MutableList<Order>>

    @GET("customer/history/")
    fun getHistory(@Header("Authorization") header: String): Single<MutableList<Order>>
}