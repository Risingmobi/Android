package com.alex.tur.data.pref

interface PrefManager {
    fun setFirstLaunch(isFirstLaunch: Boolean)
    fun isFirstLaunch(): Boolean
    fun getTrackingOrderId(): Int
    fun setTrackingOrderId(id: Int?)
}