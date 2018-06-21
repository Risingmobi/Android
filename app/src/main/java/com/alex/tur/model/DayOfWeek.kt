package com.alex.tur.model

import com.google.gson.annotations.SerializedName

enum class DayOfWeek(val day: String) {

    @SerializedName("Sunday")
    SUNDAY("Sunday"),

    @SerializedName("Monday")
    MONDAY("Monday"),

    @SerializedName("Tuesday")
    TUESDAY("Tuesday"),

    @SerializedName("Wednesday")
    WEDNESDAY("Wednesday"),

    @SerializedName("Thursday")
    THURSDAY("Thursday"),

    @SerializedName("Friday")
    FRIDAY("Friday"),

    @SerializedName("Saturday")
    SATURDAY("Saturday")
}