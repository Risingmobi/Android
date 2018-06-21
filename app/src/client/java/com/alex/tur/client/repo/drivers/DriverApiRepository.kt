package com.alex.tur.client.repo.drivers

import com.alex.tur.model.Driver
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DriverApiRepository {

    @GET("drivers/")
    fun getDrivers(
            @Header("Authorization") header: String,
            @Query("lb_lat") swLatitude: Double,
            @Query("lb_lng") swLongitude: Double,
            @Query("rt_lat") neLatitude: Double,
            @Query("rt_lng") neLongitude: Double): Single<MutableList<Driver>>
}