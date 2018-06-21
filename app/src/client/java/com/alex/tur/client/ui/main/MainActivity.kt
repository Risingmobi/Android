package com.alex.tur.client.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.base.BaseFragment
import com.alex.tur.client.ui.map.MapFragment
import com.alex.tur.client.ui.profile.ProfileFragment
import com.alex.tur.client.ui.orders.FragmentOrders
import com.alex.tur.helper.LoadingUiHelper
import kotlinx.android.synthetic.client.activity_main.*

open class MainActivity: BaseActivity(), FragmentOrders.Callback {

    private val fragmentList = mutableMapOf<String, BaseFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is BaseFragment) {
                fragment.tag?.also {
                    fragmentList[it] = fragment
                }
            }
        }

        if (savedInstanceState == null) {
            setMapFragment()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.action_map -> {
                    setMapFragment()
                }
                R.id.action_list -> {
                    setOrdersFragment()
                }
                R.id.action_profile -> {
                    setProfileFragment()
                }
            }
            true
        }
    }

    private fun replaceFragment(@IdRes containerViewId: Int, tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_out)

        var fragment = fragmentList[tag]

        if (fragment != null) {
            fragmentTransaction.attach(fragment)
        } else {
            fragment = getFragment(tag)
            fragmentList[tag] = fragment
            fragmentTransaction.add(containerViewId, fragment, tag)
        }

        for((t, f) in fragmentList) {
            if (!f.isDetached && t != tag) {
                fragmentTransaction.detach(f)
            }
        }

        fragmentTransaction.commit()
    }

    private fun setProfileFragment() {
        replaceFragment(R.id.container, ProfileFragment.TAG)
    }

    private fun setMapFragment() {
        replaceFragment(R.id.container, MapFragment.TAG)
    }

    private fun setOrdersFragment() {
        replaceFragment(R.id.container, FragmentOrders.TAG)
    }

    private fun getFragment(tag: String): BaseFragment {
        when(tag) {
            MapFragment.TAG -> {
                return MapFragment()
            }
            FragmentOrders.TAG -> {
                return FragmentOrders()
            }
            ProfileFragment.TAG -> {
                return ProfileFragment()
            }
        }
        throw IllegalArgumentException("Unknown fragment type")
    }

    override fun goToMapFromOrderList() {
        setMapFragment()
        bottomNavigationView.selectedItemId = R.id.action_map
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }
}