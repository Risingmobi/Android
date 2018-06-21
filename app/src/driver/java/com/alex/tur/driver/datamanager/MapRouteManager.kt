package com.alex.tur.driver.datamanager

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.location.Location
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import com.alex.tur.R
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.ext.buildEndLatLng
import com.alex.tur.ext.buildLine
import com.alex.tur.ext.fetchLatLng
import com.alex.tur.model.DriverPath
import com.alex.tur.model.Order
import com.alex.tur.utils.BitmapUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.driver.pending_order_map_info_view.view.*
import timber.log.Timber
import javax.inject.Inject

class MapRouteManager @Inject constructor(
        @QualifierAppContext val context: Context,
        val lifecycleOwner: LifecycleOwner): LifecycleObserver {

    private var googleMap: GoogleMap? = null

    private var startMarker: Marker? = null
    private var endMarker: Marker? = null

    private var polyline: Polyline? = null

    private var currentLocation: Location? = null

    private var accuracyCircle: Circle? = null

    private val pendingOrderMarkers = mutableListOf<OrderMarker>()

    fun attachGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override fun getInfoWindow(p0: Marker?): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                if (marker.tag is Order) {
                    val order = marker.tag as Order
                    val view = LayoutInflater.from(context).inflate(R.layout.pending_order_map_info_view, null)
                    view.nameTextView.text = order.assignTo?.name
                    view.addressTextView.text = order.address
                    view.namingTextView.text = order.service?.naming
                    return view
                }

                return null
            }
        })
    }

    fun drawRoute(driverPath: DriverPath?) {
        Timber.tag("OrderDataManagerImpl").d("drawRoute %s", driverPath?.route?.size)
        polyline?.remove()
        endMarker?.remove()
        driverPath?.also { path ->
            val polylineOptions = PolylineOptions()
            path.route?.also { route ->
                for (line in route) {
                    line.buildLine()?.also {
                        polylineOptions.add(it[0], it[1])
                    }
                }
            }

            polylineOptions.color(ContextCompat.getColor(context, R.color.colorAccent))
            polyline = googleMap?.addPolyline(polylineOptions)

            path.buildEndLatLng()?.also {
                endMarker = googleMap?.addMarker(MarkerOptions()
                        .position(it)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getTintBitmapFromResource(context, R.drawable.ic_home_black_24dp, R.color.colorAccent))))
            }
        }
    }

    fun updateCurrentLocation(location: Location?) {
        currentLocation = location

        location?.also {
            if (startMarker == null) {
                startMarker = googleMap?.addMarker(MarkerOptions()
                        .position(it.fetchLatLng())
                        .flat(true)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmapFromResource(context, R.drawable.ic_navigation_accent_24dp))))
            } else {
                startMarker?.position = it.fetchLatLng()
            }

            startMarker?.rotation = it.bearing


            if (accuracyCircle == null) {
                accuracyCircle = googleMap?.addCircle(CircleOptions()
                        .center(it.fetchLatLng())
                        .fillColor(ContextCompat.getColor(context, R.color.colorFillAccuracy))
                        .strokeWidth(0f)
                        .radius(it.accuracy.toDouble()))
            } else {
                accuracyCircle?.center = it.fetchLatLng()
            }
            accuracyCircle?.radius = it.accuracy.toDouble()

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        googleMap = null
    }

    fun drawCustomerPendingMarkers(data: MutableList<Order>?) {
        Timber.d("drawCustomerPendingMarkers %s", data?.size)
        for (orderMarker in pendingOrderMarkers) {
            orderMarker.marker.remove()
        }
        pendingOrderMarkers.clear()
        data?.also { orders ->
            for (order in orders) {
                order.fetchLatLng()?.also { latLng ->
                    val marker = googleMap?.addMarker(MarkerOptions()
                            .position(latLng)
                            .flat(true)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getTintBitmapFromResource(context, R.drawable.ic_home_black_24dp, R.color.colorIcon))))
                    marker?.also {
                        it.tag = order
                        pendingOrderMarkers.add(OrderMarker(it, order))
                    }
                }
            }
        }
    }

    private data class OrderMarker(
            var marker: Marker,
            var order: Order
    )
}