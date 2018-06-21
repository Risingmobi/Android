package com.alex.tur.driver.ui.map

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import com.alex.tur.R
import com.alex.tur.helper.GlideHelper
import com.alex.tur.model.DriverTransportMode
import com.alex.tur.utils.DimensUtils

class VehicleFloatingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val content = LayoutInflater.from(context).inflate(R.layout.floating_vehicle_view, this, true)
    private val icon: ImageView

    init {
        radius = DimensUtils.dpToPx(16f).toFloat()
        elevation = DimensUtils.dpToPx(16f).toFloat()
        icon = content.findViewById(R.id.icon)
    }

    fun seMode(mode: DriverTransportMode?) {
        when(mode) {
            DriverTransportMode.DRIVING -> {
                GlideHelper.load(context, icon, R.drawable.ic_directions_car_black_24dp)
            }
            DriverTransportMode.WALKING -> {
                GlideHelper.load(context, icon, R.drawable.ic_directions_walk_black_24dp)
            }
            DriverTransportMode.BICYCLING -> {
                GlideHelper.load(context, icon, R.drawable.ic_directions_bike_black_24dp)
            }
            null -> {

            }
        }
    }
}