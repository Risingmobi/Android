package com.alex.tur.model.api

import com.google.gson.annotations.SerializedName

data class RequestSignUp(

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)