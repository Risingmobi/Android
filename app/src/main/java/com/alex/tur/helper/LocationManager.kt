package com.alex.tur.helper

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
//import android.location.GnssStatus
import android.location.GpsStatus
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import timber.log.Timber
import java.lang.Exception

open class LocationManager(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    //private var gnssStatusCallback: GnssStatus.Callback? = null

    @Suppress("DEPRECATION")
    private var gpsStatusListener: GpsStatus.Listener? = null

    private var locationCallback : LocationCallback? = null

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 30000L
        private const val FASTEST_LOCATION_UPDATE_INTERVAL = LOCATION_UPDATE_INTERVAL / 2
    }

    fun requestLastLocation(onLastLocationCallback: LastLocationCallback) {
        if(!checkLocationPermissions()) {
            onLastLocationCallback.onRequiredLocationPermission()
        } else {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        onLastLocationCallback.onSuccess(it)
                    }
                    .addOnFailureListener {
                        Timber.e(it, "LocationManager lastLocation")
                        onLastLocationCallback.onFailure(it)
                    }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun registerLocationUpdatesListener(callback: LocationUpdatesCallback,
                                        interval: Long = LOCATION_UPDATE_INTERVAL,
                                        fastestInterval: Long = FASTEST_LOCATION_UPDATE_INTERVAL): Boolean {
        if(!checkLocationPermissions()) {
            return false
        } else {
            unregisterLocationUpdatesListener()
            locationCallback = object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    callback.onLocationChanged(locationResult.lastLocation)
                }
            }
            Timber.d("requestLocationUpdates this: %s, locationCallback: %s", hashCode(), locationCallback?.hashCode())
            fusedLocationClient.requestLocationUpdates(createLocationRequest(interval, fastestInterval), locationCallback, null)
        }
        return true
    }

    fun checkLocationSettings(locationSettingsCallback: LocationSettingsCallback) {
        val builder = LocationSettingsRequest.Builder()
                .setAlwaysShow(true)
                .addLocationRequest(createLocationRequest())
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            Timber.d("LocationManager checkLocationSettings OnSuccess %s", locationSettingsResponse.locationSettingsStates.isGpsUsable)
            if (locationSettingsResponse.locationSettingsStates.isGpsUsable) {
                locationSettingsCallback.onGpsUsable()
            }
        }
        task.addOnFailureListener { exception ->
            Timber.w(exception, "LocationManager checkLocationSettings OnFailure")
            if (exception is ResolvableApiException) {
                locationSettingsCallback.onResolutionRequired(exception)
            }
        }
    }

    fun startResolutionForResult(activity: Activity, exception: ResolvableApiException, requestCode: Int) {
        try {
            exception.startResolutionForResult(activity, requestCode)
        } catch (sendEx: IntentSender.SendIntentException) {
            Timber.e(sendEx, "startResolutionForResult")
        }
    }

    fun unregisterLocationUpdatesListener() {
        Timber.d("unregisterLocationUpdatesListener this: %s, locationCallback: %s", hashCode(), locationCallback?.hashCode())
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (exc: Exception) {
            Timber.w("unregisterLocationUpdatesListener ${exc.message}")
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    fun registerGpsStatusListener(gpsStatusCallback: GpsStatusCallback) {
        if (!checkLocationPermissions()) {
            gpsStatusCallback.onRequiredLocationPermission()
        } else {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                gnssStatusCallback = object : GnssStatus.Callback() {
                    override fun onStarted() {
                        Timber.i("GpsStatusListener started")
                        gpsStatusCallback.onStarted()
                    }
                    override fun onStopped() {
                        Timber.i("GpsStatusListener stopped")
                        gpsStatusCallback.onStopped()
                    }
                }
                Timber.w("registerGnssStatusCallback this: %s, gnssStatusCallback: %s", this.hashCode(), gnssStatusCallback?.hashCode())
                locationManager.registerGnssStatusCallback(gnssStatusCallback)
            } else {*/
                gpsStatusListener = GpsStatus.Listener { event ->
                    when (event) {
                        GpsStatus.GPS_EVENT_STARTED -> {
                            Timber.d("GPS_EVENT_STARTED")
                            gpsStatusCallback.onStarted()
                        }
                        GpsStatus.GPS_EVENT_STOPPED -> {
                            Timber.d("GPS_EVENT_STOPPED")
                            gpsStatusCallback.onStopped()
                        }
                    }
                }
                locationManager.addGpsStatusListener(gpsStatusListener)
            //}
        }
    }

    @Suppress("DEPRECATION")
    fun unregisterGpsStatusListener() {
        //Timber.w("unregisterGpsStatusListener this: %s, gnssStatusCallback: %s", this.hashCode(), gnssStatusCallback?.hashCode())
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
//        } else {
            locationManager.removeGpsStatusListener(gpsStatusListener)
//        }
    }

    private fun createLocationRequest(interval: Long = LOCATION_UPDATE_INTERVAL,
                                      fastestInterval: Long = FASTEST_LOCATION_UPDATE_INTERVAL): LocationRequest {
        return LocationRequest().apply {
            this.interval = interval
            this.fastestInterval = fastestInterval
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }








    open class LastLocationCallback {
        open fun onSuccess(location: Location?){}
        open fun onFailure(exception: Exception){}
        open fun onRequiredLocationPermission(){}
    }

    open class LocationSettingsCallback {
        open fun onResolutionRequired(exception: ResolvableApiException){}
        open fun onGpsUsable(){}
    }

    open class LocationUpdatesCallback {
        open fun onLocationChanged(location: Location?){}
        open fun onRequiredLocationPermission(){}
    }

    open class GpsStatusCallback {
        open fun onStarted() { }
        open fun onStopped() { }
        open fun onRequiredLocationPermission(){}
    }
}
