package com.alex.tur.driver.ui.map

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Rect
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.base.MapManager
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.driver.broadcast.DriverLocationReceiver
import com.alex.tur.driver.datamanager.MapRouteManager
import com.alex.tur.driver.ui.main.MainViewModel
import com.alex.tur.helper.Result
import com.alex.tur.model.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.driver.fragment_map.*
import timber.log.Timber
import javax.inject.Inject

class MapFragment: BaseFragment(), VehicleDialogFragment.Listener, MapManager.Callback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MainViewModel>

    lateinit var viewModel: MainViewModel

    lateinit var callback: Callback

    @Inject
    lateinit var mapManager: MapManager

    @Inject
    lateinit var mapRouteManager: MapRouteManager

    private val transportModeActionObserver = Observer<Result<DriverTransportMode>> {
        it?.let {
            VehicleDialogFragment.newInstance(it).show(childFragmentManager, VehicleDialogFragment.TAG)
        }
    }
    private val profileObserver = Observer<Result<Driver>> {
        when(it?.status) {
            Result.Status.SUCCESS -> {
                avatarView.setAvatar(it.data?.avatar)
            }
        }
    }
    private val activeOrderObserver = Observer<Result<Order>> {
        mapRouteManager.drawRoute(it?.data?.driverPath)
        when(it?.status) {
            Result.Status.SUCCESS -> {

            }
            Result.Status.ERROR -> {
                showError(it.message)
            }
            Result.Status.LOADING -> {}
            null -> {}
        }
    }
    private val statusObserver = Observer<Result<DriverStatus>> {
        avatarView.setStatus(it?.data)
        workingView.setStatus(it?.data)
    }
    private val transportModeObserver = Observer<Result<DriverTransportMode>> {
        it?.let {
            vehicleView.seMode(it.data)
            val vehicleDialogFragment = childFragmentManager.findFragmentByTag(VehicleDialogFragment.TAG) as VehicleDialogFragment?
            vehicleDialogFragment?.setResultData(it)
        }
    }

    private val pendingOrderListObserver = Observer<Result<MutableList<Order>>> {
        mapRouteManager.drawCustomerPendingMarkers(it?.data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriverLocationReceiver.registerLocationUpdates(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync(mapManager)
        mapManager.setCallback(this)
        mapManager.hasMyLocationMarker = false

        myLocationFab.setOnClickListener {
            mapManager.animateToMyLocation()
        }
        startWorkingBtn.setOnClickListener {
            viewModel.changeStatus(DriverStatus.ACTIVE)
        }
        cancelWorkingBtn.setOnClickListener {
            viewModel.changeStatus(DriverStatus.INACTIVE)
        }
        avatarView.setOnClickListener({ callback.onProfileClicked() })
        vehicleView.setOnClickListener {
            viewModel.onTransportModeClicked()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)
        lifecycle.addObserver(viewModel)
        viewModel.profile.observe(viewLifecycleOwner, profileObserver)
        viewModel.transportModeAction.observe(viewLifecycleOwner, transportModeActionObserver)
        viewModel.status.observe(viewLifecycleOwner, statusObserver)
        viewModel.transportMode.observe(viewLifecycleOwner, transportModeObserver)

        viewModel.requestProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.profile.removeObserver(profileObserver)
        viewModel.transportModeAction.removeObserver(transportModeActionObserver)
        viewModel.status.removeObserver(statusObserver)
        viewModel.transportMode.removeObserver(transportModeObserver)
        viewModel.activeOrder.removeObserver(activeOrderObserver)
        viewModel.pendingOrderList.removeObserver(pendingOrderListObserver)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.uiSettings.isIndoorLevelPickerEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = false

        mapRouteManager.attachGoogleMap(googleMap)
        viewModel.activeOrder.observe(viewLifecycleOwner, activeOrderObserver)
        viewModel.pendingOrderList.observe(viewLifecycleOwner, pendingOrderListObserver)

        viewModel.requestActiveOrder()
        viewModel.requestPendingOrderList(false)
    }

    override fun requestPermissions() {
        requestLocationPermissions()
    }

    override fun onLocationChanged(location: Location?) {
        mapRouteManager.updateCurrentLocation(location)
    }

    override fun onCameraMovedOnStart() {

    }

    override fun onPermissionLocationGranted() {
        DriverLocationReceiver.registerLocationUpdates(context!!)
        mapManager.onPermissionLocationGranted()
    }

    override fun onGpsStarted() {
//        myLocationFab.setImageResource(R.drawable.ic_my_location_black_24dp)
    }

    override fun onGpsStopped() {
//        myLocationFab.setImageResource(R.drawable.ic_location_disabled_black_24dp)
    }

    override fun onTransportModeSelected(driverTransportMode: DriverTransportMode) {
        viewModel.changeTransportMode(driverTransportMode)
    }

    fun onOrderListBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        val bottomSheetGlobalRect = Rect()
        bottomSheet.getGlobalVisibleRect(bottomSheetGlobalRect)

        val viewGlobalRect = Rect()
        view?.getGlobalVisibleRect(viewGlobalRect)

        val viewLocalRect = Rect()
        view?.getLocalVisibleRect(viewLocalRect)

        if (bottomSheetGlobalRect.top < (myLocationFab.bottom  + (viewGlobalRect.top - viewLocalRect.top) + myLocationFab.paddingBottom)) {
            myLocationFab.translationY = ((bottomSheetGlobalRect.top - viewGlobalRect.bottom) + (viewLocalRect.bottom - myLocationFab.bottom) - myLocationFab.paddingBottom).toFloat()
        } else {
            myLocationFab.translationY = 0f
        }

        if (bottomSheetGlobalRect.top < viewGlobalRect.bottom) {
            mapManager.googleMap?.setPadding(0,0,0,(viewGlobalRect.bottom - bottomSheetGlobalRect.top))
        } else {
            mapManager.googleMap?.setPadding(0,0,0,0)
        }
    }

    fun onOrderListBottomSheetStateChanged(bottomSheet: View, newState: Int) {
        when(newState) {
            BottomSheetBehavior.STATE_HIDDEN -> {
                startWorkingBtn.visibility = View.VISIBLE
                startWorkingBtn.animate().setDuration(150).alpha(1f).withEndAction {
                    startWorkingBtn.isEnabled = true
                }
            }
            else -> {
                startWorkingBtn.animate().setDuration(150).alpha(0f).withEndAction {
                    startWorkingBtn.visibility = View.INVISIBLE
                    startWorkingBtn.isEnabled = false
                }
            }
        }
    }

    interface Callback {
        fun onProfileClicked()
    }

    companion object {
        const val TAG = "MapFragment"
    }
}