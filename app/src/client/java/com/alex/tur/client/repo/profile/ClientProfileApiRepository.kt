package com.alex.tur.client.repo.profile

import com.alex.tur.model.Customer
import com.alex.tur.model.ResponseAvatar
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ClientProfileApiRepository {

    @GET("customer/")
    fun getProfile(@Header("Authorization") header: String): Single<Customer>

    @GET("customer/")
    fun getProfileTest(@Header("Authorization") header: String): Call<Customer>

    @PATCH("customer/")
    fun updateProfile(@Header("Authorization") header: String, @Body customer: Customer): Single<Customer>

    @Multipart
    @PUT("customer/avatar/")
    fun updateAvatar(@Header("Authorization") header: String,
                     @Part file: MultipartBody.Part): Single<ResponseAvatar>
}