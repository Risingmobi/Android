package com.alex.tur.driver.repo.profile

import com.alex.tur.model.Driver
import com.alex.tur.model.Service
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface DriverProfileApiRepository {

    @GET("driver/")
    fun getProfile(@Header("Authorization") header: String): Single<Driver>

    @PATCH("driver/")
    fun updateProfile(@Header("Authorization") header: String, @Body driver: Driver): Single<Driver>

    @GET("services/")
    fun getServices(@Header("Authorization") header: String): Single<MutableList<Service>>
}