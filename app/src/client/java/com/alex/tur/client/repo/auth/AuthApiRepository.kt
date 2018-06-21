package com.alex.tur.client.repo.auth

import com.alex.tur.model.Customer
import com.alex.tur.model.api.ResponseToken
import com.alex.tur.model.api.RequestSignUp
import io.reactivex.Single
import retrofit2.http.*

interface AuthApiRepository {

    @POST("customers/")
    fun signUp(@Body requestData: RequestSignUp): Single<Customer>

    @POST("o/token/")
    @FormUrlEncoded
    fun login(
            @Header("Authorization") header: String,
            @Field("grant_type") grantType: String,
            @Field("username") email: String,
            @Field("password") password: String): Single<ResponseToken>
}