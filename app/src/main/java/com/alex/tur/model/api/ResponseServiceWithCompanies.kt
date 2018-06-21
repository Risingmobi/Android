package com.alex.tur.model.api

import com.google.gson.annotations.SerializedName
import com.alex.tur.model.Company

data class ResponseServiceWithCompanies(
        @SerializedName("companies")
        var companies: MutableList<Company>? = null,

        @SerializedName("naming")
        var naming: String? = null,

        @SerializedName("picture")
        var picture: String? = null
)