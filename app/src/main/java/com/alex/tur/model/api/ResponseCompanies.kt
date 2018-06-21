package com.alex.tur.model.api

import com.alex.tur.model.Company
import com.google.gson.annotations.SerializedName

data class ResponseCompanies(
        @SerializedName("companies")
        var companies: MutableList<Company>? = null
)