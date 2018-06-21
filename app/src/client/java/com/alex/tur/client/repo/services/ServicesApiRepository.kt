package com.alex.tur.client.repo.services

import com.alex.tur.model.api.ResponseServiceWithCompanies
import com.alex.tur.model.Service
import com.alex.tur.model.api.ResponseCompanies
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ServicesApiRepository {

    @GET("services/")
    fun getAllServices(@Header("Authorization") header: String): Single<MutableList<Service>>

    @GET("services_with_companies/{service_naming}/")
    fun getCompaniesByServiceName(
            @Header("Authorization") header: String,
            @Path("service_naming") naming: String): Single<ResponseCompanies>

    @GET("services_with_companies/")
    fun getServicesWithCompanies(
            @Header("Authorization") header: String): Single<MutableList<ResponseServiceWithCompanies>>
}