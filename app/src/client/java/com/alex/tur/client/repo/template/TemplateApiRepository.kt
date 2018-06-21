package com.alex.tur.client.repo.template

import com.alex.tur.model.Template
import com.alex.tur.model.api.ResponseTemplate
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface TemplateApiRepository {

    @GET("schedule/")
    fun getTemplates(@Header("Authorization") header: String): Single<MutableList<ResponseTemplate>>

    @Multipart
    @POST("schedule/")
    fun uploadTemplate(
            @Header("Authorization") header: String,
            @Part picturePart: MultipartBody.Part?,
            @Part("lat") latPart: RequestBody,
            @Part("lng") lngPart: RequestBody,
            @Part("time") timePart: RequestBody,
            @Part("day_of_week") dayOfWeekPart: RequestBody,
            @Part("request_descr_briefly_description") commentPart: RequestBody?,
            @Part("service_descr_id") serviceIdPart: RequestBody
    ): Single<Unit>
}