package com.alex.tur.driver.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.alex.tur.R
import com.alex.tur.driver.ui.map.MapFragment
import com.alex.tur.driver.ui.profile.ProfileFragment
import com.alex.tur.base.BaseActivity
import com.alex.tur.driver.ui.orderlist.OrderListFragment

class MainActivity: BaseActivity(), MapFragment.Callback, OrderListFragment.Callback {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    private lateinit var ordersFragment: OrderListFragment

    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ordersFragment = supportFragmentManager.findFragmentById(R.id.orderListFragment) as OrderListFragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as MapFragment
    }

    override fun onProfileClicked() {
        setProfileFragment()
    }

    private fun setProfileFragment() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_out, R.animator.fragment_fade_in, R.animator.fragment_fade_out)
                .add(R.id.container, ProfileFragment(), ProfileFragment.TAG)
                .addToBackStack("stack")
                .commit()
    }

    override fun onBackPressed() {
        if (!ordersFragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onOrderListBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        mapFragment.onOrderListBottomSheetSlide(bottomSheet, slideOffset)
    }

    override fun onOrderListBottomSheetStateChanged(bottomSheet: View, newState: Int) {
        mapFragment.onOrderListBottomSheetStateChanged(bottomSheet, newState)
    }
}