package com.alex.tur.base

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.pm.PackageManager
import android.location.Location
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.ActivityCompat
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.LocationManager
import com.alex.tur.utils.BitmapUtils
import com.alex.tur.utils.DimensUtils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.reactivex.disposables.Disposable
import timber.log.Timber

class MapManager(val activity: Activity, val lifecycleOwner: LifecycleOwner): OnMapReadyCallback, LifecycleObserver {

    var googleMap: GoogleMap? = null

    lateinit var locationManager: LocationManager

    lateinit var addressFetcher: AddressFetcher

    private val noPermissionActions = mutableSetOf<Runnable>()

    private var gpsResolutionRequired = false

    private var addressDisposable: Disposable? = null

    var moveToMyLocationOnCreate = true

    var hasMyLocationMarker = true

    private var callback: Callback? = null

    private val enableMyLocationAction = Runnable {
        if (hasMyLocationMarker) {
            googleMap?.isMyLocationEnabled = true
        }

        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        Timber.w("enableMyLocationAction %s", moveToMyLocationOnCreate)
        if (moveToMyLocationOnCreate) {
            locationManager.requestLastLocation(object : LocationManager.LastLocationCallback() {
                override fun onSuccess(location: Location?) {
                    location?.let {
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), DEFAULT_ZOOM))
                        callback?.onCameraMovedOnStart()
                    }
                }
            })
        }
    }

    companion object {
        const val DEFAULT_ZOOM = 13f
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        locationManager = LocationManager(activity)
        addressFetcher = AddressFetcher(activity)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        moveToMyLocationOnCreate = false
        googleMap = null
        addressDisposable?.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        locationManager.unregisterGpsStatusListener()
        locationManager.unregisterLocationUpdatesListener()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume")
        gpsResolutionRequired = false
        if (checkLocationPermissions()) {
            locationManager.registerLocationUpdatesListener(object : LocationManager.LocationUpdatesCallback() {
                override fun onLocationChanged(location: Location?) {
                    Timber.d("LocationUpdate location %s", location)
                    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                        callback?.onLocationChanged(location)
                    }
                }
                override fun onRequiredLocationPermission() {
                    Timber.d("LocationUpdate onRequiredLocationPermission")
                }
            })
            locationManager.registerGpsStatusListener(object : LocationManager.GpsStatusCallback() {
                override fun onStarted() {
                    Timber.d("gpsStatus onStarted")
                    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                        callback?.onGpsStarted()
                    }
                }
                override fun onStopped() {
                    Timber.d("gpsStatus onStopped")
                    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                        callback?.onGpsStopped()
                    }
                }
                override fun onRequiredLocationPermission() {
                    Timber.d("gpsStatus onRequiredLocationPermission")
                }
            })
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        if (checkLocationPermissions()) {
            enableMyLocationAction.run()
        } else {
            noPermissionActions.add(enableMyLocationAction)
            Timber.d("add enableMyLocationAction %s", noPermissionActions.size)
            callback?.requestPermissions()
        }

        callback?.onMapReady(googleMap)
    }

    fun onPermissionLocationGranted() {
        noPermissionActions.forEach { it.run() }
        noPermissionActions.clear()
    }

    fun animateToMyLocation(zoom: Float = DEFAULT_ZOOM) {
        applyIfGpsAvaliable {
            applyIfLastLocationAvaliable {
                animateToLocation(it, zoom)
            }
        }
    }

    fun moveToMyLocation(zoom: Float = DEFAULT_ZOOM) {
        applyIfGpsAvaliable {
            applyIfLastLocationAvaliable {
                moveToLocation(it, zoom)
            }
        }
    }



    fun applyIfGpsAvaliable(apply: () -> Unit) {
        locationManager.checkLocationSettings(object : LocationManager.LocationSettingsCallback() {
            override fun onGpsUsable() {
                apply()
            }
            override fun onResolutionRequired(exception: ResolvableApiException) {
                if (!gpsResolutionRequired) {
                    gpsResolutionRequired = true
                    locationManager.startResolutionForResult(activity, exception, 12354)
                }
            }
        })
    }

    fun applyIfLastLocationAvaliable(apply: (Location?) -> Unit) {
        locationManager.requestLastLocation(object : LocationManager.LastLocationCallback() {
            override fun onSuccess(location: Location?) {
                apply(location)
            }
            override fun onRequiredLocationPermission() {
                callback?.requestPermissions()
            }
        })
    }

    /*fun setOnAddressChangeOnCameraMoveListener(addressFunction: (MyAddress?) -> Unit) {
        Timber.d("setOnAddressChangeOnCameraMoveListener %s", googleMap)
        googleMap?.setOnCameraIdleListener {
            if (googleMap != null) {
                addressDisposable?.dispose()
                val latLng = googleMap?.cameraPosition?.target
                latLng?.let {

                    *//*val res = launch {
                        addressFetcher.fetchAddressAsync(latLng)
                    }

                    res.invokeOnCompletion {

                    }*//*
                    addressDisposable = Single.fromCallable { addressFetcher.fetchAddressAsync(latLng) }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                addressDisposable?.dispose()
                                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                                    it?.type = MyAddress.Type.SELECTED
                                    addressFunction(it)
                                }
                            }, {
                                addressDisposable?.dispose()
                                Timber.e(it)
                            })
                }
            }
        }
    }*/

    fun animateToBounds(latLngBounds: LatLngBounds?, paddingDp: Int = 32) {
        latLngBounds?.also {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(it, DimensUtils.dpToPx(paddingDp.toFloat())))
        }
    }

    fun animateToLocation(latLng: LatLng?) {
        animateToLocation(latLng, DEFAULT_ZOOM)
    }

    fun animateToLocation(location: Location?) {
        animateToLocation(location, DEFAULT_ZOOM)
    }

    fun animateToLocation(location: Location?, zoom: Float) {
        if (location != null) {
            animateToLocation(LatLng(location.latitude, location.longitude), zoom)
        }
    }

    fun animateToLocation(latLng: LatLng?, zoom: Float) {
        latLng?.let {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    fun moveToLocation(latLng: LatLng?) {
        moveToLocation(latLng, DEFAULT_ZOOM)
    }

    fun moveToLocation(location: Location?) {
        moveToLocation(location, DEFAULT_ZOOM)
    }

    fun moveToLocation(location: Location?, zoom: Float) {
        if (location != null) {
            moveToLocation(LatLng(location.latitude, location.longitude), zoom)
        }
    }

    fun moveToLocation(latLng: LatLng?, zoom: Float) {
        latLng?.let {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    fun addMarker(latLng: LatLng, @DrawableRes iconRes: Int, @ColorRes colorRes: Int = android.R.color.black): Marker? {
        val bm = BitmapUtils.getTintBitmapFromResource(activity, iconRes, colorRes)
        val markerOptions = MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bm))
        return googleMap?.addMarker(markerOptions)
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    interface Callback {
        fun onMapReady(googleMap: GoogleMap)
        fun onGpsStarted()
        fun onGpsStopped()
        fun requestPermissions()
        fun onLocationChanged(location: Location?)
        fun onCameraMovedOnStart()
    }
}
