package com.alex.tur.client.ui.address.map

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.base.MapManager
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.Result
import com.alex.tur.model.MyAddress
import com.alex.tur.ui.profile.edit.QualifierAddressMapAction
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_address_map.*
import kotlinx.android.synthetic.main.floating_address_view.*
import timber.log.Timber
import javax.inject.Inject

class AddressMapActivity: BaseActivity(), MapManager.Callback {

    @Inject
    lateinit var addressFetcher: AddressFetcher

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AddressViewModel>

    lateinit var viewModel: AddressViewModel

    @Inject
    @JvmField
    @field:QualifierAddressMapAction
    var action: String? = null

    lateinit var mapManager: MapManager

    companion object {

        const val ZOOM = 16f

        const val ACTION_CHANGE_HOME = "ACTION_CHANGE_HOME"
        const val ACTION_GET_ADDRESS = "ACTION_GET_ADDRESS"
        const val EXTRA_MY_ADDRESS = "EXTRA_MY_ADDRESS"

        fun start(activity: Activity?, address: MyAddress?) {
            activity?.let {
                it.startActivity(Intent(it, AddressMapActivity::class.java).apply {
                    putExtra(EXTRA_MY_ADDRESS, address)
                    action = ACTION_CHANGE_HOME
                })
            }
        }

        fun startForResult(activity: Activity?, address: MyAddress?, requestCode: Int) {
            activity?.let {
                it.startActivityForResult(Intent(it, AddressMapActivity::class.java).apply {
                    putExtra(EXTRA_MY_ADDRESS, address)
                    action = ACTION_GET_ADDRESS
                }, requestCode)
            }
        }

        fun startForResult(activity: Activity?, address: MyAddress?, act: String, requestCode: Int) {
            activity?.let {
                it.startActivityForResult(Intent(it, AddressMapActivity::class.java).apply {
                    putExtra(EXTRA_MY_ADDRESS, address)
                    action = act
                }, requestCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_map)
        Timber.d("onCreate")

        mapManager = MapManager(this, this)
        lifecycle.addObserver(mapManager)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(mapManager)
        mapManager.setCallback(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddressViewModel::class.java)
        viewModel.addressAction.observe(this, Observer {
            it?.let {
                when(it.status) {
                    Result.Status.LOADING -> {
                        showLoading()
                    }
                    Result.Status.SUCCESS -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    Result.Status.ERROR -> {
                        hideLoading()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        viewModel.address.observe(this, Observer { address ->
            address?.let {
                addressTextView.text = it.addressString
            }
        })

        applyBtn.setOnClickListener {
            changeAddress(viewModel.address.value)
        }

        myLocationFab.setOnClickListener {
            mapManager.animateToMyLocation(ZOOM)
        }

        backBtn.setOnClickListener {
            finish()
        }

        viewModel.address.value?.let {
            mapManager.moveToMyLocationOnCreate = false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        viewModel.address.value?.let {
            mapManager.moveToLocation(it.latLng)
        }

        googleMap.setOnCameraIdleListener {
            val type = if (action == ACTION_CHANGE_HOME) {
                MyAddress.Type.HOME
            } else {
                MyAddress.Type.SELECTED
            }
            addressFetcher.fetchAddressAsync(googleMap.cameraPosition?.target, type)
                    .subscribe({
                        viewModel.address.value = it
                    }, {
                        Timber.e(it)
                    })
        }
    }

    override fun onGpsStarted() {
        myLocationFab.setImageResource(R.drawable.ic_my_location_black_24dp)
    }

    override fun onGpsStopped() {
        myLocationFab.setImageResource(R.drawable.ic_location_disabled_black_24dp)
    }

    override fun requestPermissions() {
        requestLocationPermissions()
    }

    override fun onPermissionLocationGranted() {
        mapManager.onPermissionLocationGranted()
    }

    override fun onLocationChanged(location: Location?) {

    }

    fun changeAddress(address: MyAddress?) {
        Timber.d("address %s", address)
        if (address != null) {
            when(action) {
                ACTION_CHANGE_HOME -> {
                    viewModel.changeAddress(address)
                }
                ACTION_GET_ADDRESS -> {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(EXTRA_MY_ADDRESS, address)
                    })
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCameraMovedOnStart() {

    }
}