package com.alex.tur.model

import com.google.gson.annotations.SerializedName

enum class PaymentStatus {
    @SerializedName("Credit card")
    CREDIT_CARD,
    @SerializedName("Invoice")
    INVOICE
}