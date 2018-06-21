package com.alex.tur.data.pref

import android.content.SharedPreferences
import com.alex.tur.AppConstants.INVALID_ID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefManagerImpl @Inject constructor(private val pref: SharedPreferences): PrefManager {

    companion object {
        private const val KEY_TRACKING_ORDER_ID = "KEY_TRACKING_ORDER_ID"
        private const val KEY_IS_FIRST_LAUNCH = "KEY_IS_FIRST_LAUNCH"
    }

    override fun setFirstLaunch(isFirstLaunch: Boolean) {
        pref.edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    override fun isFirstLaunch(): Boolean {
        return pref.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    override fun getTrackingOrderId(): Int {
        return pref.getInt(KEY_TRACKING_ORDER_ID, INVALID_ID)
    }

    override fun setTrackingOrderId(id: Int?) {
        pref.edit().putInt(KEY_TRACKING_ORDER_ID, id ?: INVALID_ID).apply()
    }
}