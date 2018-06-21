package com.alex.tur.client.datamanager

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.support.v4.content.ContextCompat
import com.alex.tur.R
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.ext.buildEndLatLng
import com.alex.tur.ext.buildLine
import com.alex.tur.ext.buildStartLatLng
import com.alex.tur.ext.fetchLatLng
import com.alex.tur.model.*
import com.alex.tur.utils.BitmapUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import timber.log.Timber
import javax.inject.Inject

class MapRouteManager @Inject constructor(
        @QualifierAppContext val context: Context,
        val lifecycleOwner: LifecycleOwner): LifecycleObserver {

    private var googleMap: GoogleMap? = null

    private var homeMarker: Marker? = null

    private val driverMarkers = mutableListOf<DriverMarker>()
    private var driverList: MutableList<Driver>? = null

    private var callback: Callback? = null

    private var isDriverRouteDrawing = false

    private var trackingOrder: Order? = null

    private var driverStartMarker: Marker? = null
    private var driverEndMarker: Marker? = null
    private var driverPolyline: Polyline? = null

    private val latLngBoundsHandler = Handler()
    private val latLngBoundsRunnable = object : Runnable {
        override fun run() {
            Timber.d("latLngBoundsRunnable %s", googleMap?.cameraPosition?.zoom)
            googleMap?.cameraPosition?.zoom?.also {
                if(it >= MIN_ZOOM) {
                    val latLngBounds = googleMap?.projection?.visibleRegion?.latLngBounds
                    latLngBounds?.let {
                        Timber.d("latLngBoundsRunnable requestDriverMarkers")
                        callback?.requestDriverMarkers(it)
                    }
                }
            }
            latLngBoundsHandler.postDelayed(this, DRIVERS_MARKERS_UPDATE_INTERVAL)
        }
    }

    fun attachGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        latLngBoundsHandler.removeCallbacks(latLngBoundsRunnable)
        latLngBoundsHandler.postDelayed(latLngBoundsRunnable, 2000) //kostb|lb (wait for map camera zoom in)
    }

    fun drawDriversMarkers(drivers: MutableList<Driver>?) {
        if (!isDriverRouteDrawing) {
            removeDriverMarkers()
            driverList = drivers
            drivers?.also {
                for (driver in it) {
                    addMarker(driver)
                }
            }
        }
    }

    private fun removeDriverMarkers() {
        for (driverMarker in driverMarkers) {
            driverMarker.marker.remove()
        }
        driverMarkers.clear()
    }

    private fun addMarker(driver: Driver) {
        driver.fetchLatLng()?.also { latLng ->
            val marker = googleMap?.addMarker(MarkerOptions()
                    .position(latLng)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(getIcon(driver))))
            marker?.also {
                driverMarkers.add(DriverMarker(it, driver))
            }
        }
    }

    private fun setMarkerIcon(driverMarker: DriverMarker) {
        driverMarker.marker.setIcon(BitmapDescriptorFactory.fromBitmap(getIcon(driverMarker.driver)))
    }

    private fun getIcon(driver: Driver): Bitmap? {
        return when(driver.transportMode) {
            DriverTransportMode.DRIVING -> {
                BitmapUtils.getTintBitmapFromResource(context, R.drawable.ic_directions_car_black_24dp, R.color.colorIcon)
            }
            DriverTransportMode.WALKING -> {
                BitmapUtils.getTintBitmapFromResource(context, R.drawable.ic_directions_walk_black_24dp, R.color.colorIcon)
            }
            DriverTransportMode.BICYCLING -> {
                BitmapUtils.getTintBitmapFromResource(context, R.drawable.ic_directions_bike_black_24dp, R.color.colorIcon)
            }
            else -> {
                null
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        latLngBoundsHandler.removeCallbacks(latLngBoundsRunnable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        latLngBoundsHandler.removeCallbacks(latLngBoundsRunnable)
        latLngBoundsHandler.post(latLngBoundsRunnable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        googleMap = null
    }

    fun drawHomeMarker(customer: Customer?) {
        customer?.also {
            it.fetchLatLng()?.also {
                if(homeMarker == null) {
                    homeMarker = googleMap?.addMarker(MarkerOptions()
                            .position(it)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getTintBitmapFromResource(context, R.drawable.ic_home_black_24dp, R.color.colorAccent))))
                } else {
                    homeMarker?.position = it
                }
            }
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun drawDriverRoute(needDraw: Boolean) {
        drawDriverRoute(trackingOrder, needDraw)
    }

    fun drawDriverRoute(data: Order?, needDraw: Boolean) {
        trackingOrder = data
        isDriverRouteDrawing = needDraw

        if (data != null && isDriverRouteDrawing) {
            drawDriverRoute(trackingOrder?.driverPath)
            removeDriverMarkers()
        } else {
            isDriverRouteDrawing = false
            drawDriversMarkers(driverList)
            driverPolyline?.remove()
            driverStartMarker?.remove()
            driverEndMarker?.remove()
        }
    }

    private fun drawDriverRoute(driverPath: DriverPath?) {
        driverPolyline?.remove()
        driverStartMarker?.remove()
        driverEndMarker?.remove()
        driverPath?.also { path ->
            val polylineOptions = PolylineOptions()
            path.route?.also { route ->
                for (line in route) {
                    line.buildLine()?.also {
                        polylineOptions.add(it[0], it[1])
                    }
                }
            }

            polylineOptions.color(ContextCompat.getColor(context, R.color.colorDriverPath))
            driverPolyline = googleMap?.addPolyline(polylineOptions)

            path.buildStartLatLng()?.also {
                driverStartMarker = googleMap?.addMarker(MarkerOptions()
                        .position(it)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getTintBitmapFromResource(context, R.drawable.ic_directions_car_black_24dp, R.color.colorAccent))))
            }

            path.buildEndLatLng()?.also {
                driverEndMarker = googleMap?.addMarker(MarkerOptions()
                        .position(it)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            }
        }
    }

    companion object {
        private const val DRIVERS_MARKERS_UPDATE_INTERVAL = 30000L
        private const val MIN_ZOOM = 13f
        const val ROUTE_PADDING = 80
    }

    interface Callback {
        fun requestDriverMarkers(latLngBounds: LatLngBounds)
    }

    private data class DriverMarker(
        var marker: Marker,
        var driver: Driver
    )
}