package com.alex.tur.driver.broadcast

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.driver.datamanager.location.DriverLocationDataManager
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DriverLocationReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var locationDataManager: DriverLocationDataManager

    @Inject
    lateinit var profileDataManager: DriverProfileDataManager

    @Inject
    lateinit var accountManager: MyAccountManager

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        when(intent?.action) {
            ACTION_NOT_WORKING -> {
                LocationResult.extractResult(intent)?.let {
                    sendLocation(it.lastLocation)
                }
            }
        }
    }

    private fun sendLocation(location: Location) {

        accountManager.getDriverTransportMode()?.let {
            logLocationToFireStore(location)
            locationDataManager.updateCurrentLocation(location, it)
                    .subscribe({

                    }, {
                        Timber.e(it)
                    })
        }
    }

    private fun logLocationToFireStore(location: Location) {
        /*val db = FirebaseFirestore.getInstance()
        val map = mutableMapOf<String, Any>().apply {
            put("latitude", location.latitude.toString())
            put("longitude", location.longitude.toString())
            put("receiver_hash", this@DriverLocationReceiver.hashCode().toString())
            put("time", Date().toString())
        }

        db.collection("LOCATION").document(System.currentTimeMillis().toString()).set(map)*/
    }

    companion object {

        const val ACTION_NOT_WORKING = "ACTION_NOT_WORKING"
        const val ACTION_WORKING = "ACTION_WORKING"

        private const val SMALLEST_DISPLACEMENT_METERS = 25f
        private const val UPDATE_INTERVAL_WORKING = 10000L
        private const val UPDATE_INTERVAL_NOT_WORKING = 300000L

        @SuppressLint("MissingPermission")
        fun registerLocationUpdates(context: Context) {
            if (checkLocationPermissions(context)) {
                val locationRequest = LocationRequest()
                locationRequest.interval = UPDATE_INTERVAL_WORKING
                locationRequest.fastestInterval = UPDATE_INTERVAL_WORKING
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                locationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT_METERS

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.requestLocationUpdates(locationRequest, getPendingIntent(context))
            }
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DriverLocationReceiver::class.java)
            intent.action = DriverLocationReceiver.ACTION_NOT_WORKING
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun checkLocationPermissions(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }
}
