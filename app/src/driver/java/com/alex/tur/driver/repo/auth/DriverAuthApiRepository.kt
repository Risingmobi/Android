package com.alex.tur.driver.repo.auth

import com.alex.tur.model.api.ResponseToken
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DriverAuthApiRepository {

    @POST("o/token/")
    @FormUrlEncoded
    fun login(
            @Header("Authorization") header: String,
            @Field("grant_type") grantType: String,
            @Field("username") email: String,
            @Field("password") password: String): Single<ResponseToken>
}