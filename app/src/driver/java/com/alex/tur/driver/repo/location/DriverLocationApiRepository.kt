package com.alex.tur.driver.repo.location

import com.alex.tur.model.Driver
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH

interface DriverLocationApiRepository {

    @PATCH("driver_current_position/")
    fun updateLocation(@Header("Authorization") header: String, @Body driver: Driver): Completable
}