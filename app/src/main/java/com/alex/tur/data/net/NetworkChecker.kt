package com.alex.tur.data.net

import android.content.Context
import android.net.ConnectivityManager
import com.alex.tur.di.qualifier.global.QualifierAppContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkChecker
@Inject
constructor(@QualifierAppContext private val context: Context) {

    val isConnected: Boolean
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val netInfo = cm?.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
}
