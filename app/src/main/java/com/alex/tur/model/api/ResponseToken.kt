package com.alex.tur.model.api

import com.google.gson.annotations.SerializedName

data class ResponseToken(

        @SerializedName("access_token")
        var accessToken: String? = null,

        @SerializedName("expires_in")
        var expiresIn: Long? = null,

        @SerializedName("refresh_token")
        var refreshToken: String? = null,

        @SerializedName("scope")
        var scope: String? = null,

        @SerializedName("token_type")
        var tokenType: String? = null
)