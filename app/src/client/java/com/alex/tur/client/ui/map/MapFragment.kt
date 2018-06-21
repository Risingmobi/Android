package com.alex.tur.client.ui.map

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.base.MapManager
import com.alex.tur.client.datamanager.MapRouteManager
import com.alex.tur.client.datamanager.MapRouteManager.Companion.ROUTE_PADDING
import com.alex.tur.client.ui.address.search.AddressSearchActivity
import com.alex.tur.client.ui.comment.CommentActivity
import com.alex.tur.client.ui.companies.CompanyListActivity
import com.alex.tur.client.ui.main.MainViewModel
import com.alex.tur.client.ui.request.WaitingForOrderDialogFragment
import com.alex.tur.client.ui.services.ServicesListActivity
import com.alex.tur.client.ui.template.TemplateTimeActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.buildLatLngBounds
import com.alex.tur.ext.getViewModel
import com.alex.tur.helper.GlideHelper
import com.alex.tur.helper.Result
import com.alex.tur.model.*
import com.alex.tur.model.api.ResponseTemplate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.client.fragment_map.*
import kotlinx.android.synthetic.main.floating_address_view.*
import kotlinx.android.synthetic.main.floating_address_view.view.*
import javax.inject.Inject

class MapFragment: BaseFragment(), MapManager.Callback, MapRouteManager.Callback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MainViewModel>

    lateinit var viewModel: MainViewModel

    private var selectedAddressMarker: Marker? = null

    lateinit var supportMapFragment: SupportMapFragment

    @Inject
    lateinit var mapManager: MapManager

    @Inject
    lateinit var mapRouteManager: MapRouteManager

    private val updateTrackingOrderHandler = Handler()
    private val updateTrackingOrderRunnable = object: Runnable {
        override fun run() {
            viewModel.requestTrackingOrder(true)//TODO
            updateTrackingOrderHandler.postDelayed(this, 60000)
        }
    }

    private val serviceForOrderObserver = Observer<Service> {
        bottomSheetContainer.setServiceForRequestService(it)
    }

    private val profileForOrderObserver = Observer<Result<Customer>> {
        bottomSheetContainer.setProfileForRequestService(it?.data)
    }

    private val addressForOrderObserver = Observer<MyAddress> {
        bottomSheetContainer.setAddressForRequestService(it)
    }

    private val commentForOrderObserver = Observer<OrderDescription> {
        bottomSheetContainer.setCommentForRequestService(it)
    }

    private val templateListObserver = Observer<Result<MutableList<ResponseTemplate>>> {
        bottomSheetContainer.setTemplateListData(it)
    }

    private val trackingOrderObserver = object: Observer<Result<Order?>> {

        override fun onChanged(it: Result<Order?>?) {
            bottomSheetContainer.attachTrackingOrderData(it)
        }
    }

    private val trackingOrderGlobalObserver = object: Observer<Result<Order?>> {

        override fun onChanged(it: Result<Order?>?) {
            bottomSheetContainer.setHasTrackingOrder(it?.data != null)
            if(bottomSheetContainer.isOrderDetailVisible) {
                viewModel.requestFocusOnTrackingOrderBounds()
            }
            if (it?.data != null) {
                GlideHelper.loadAvatar(context, driverImageView, it.data?.willBeEvaluatedBy?.avatar)
                driverImageView.visibility = View.VISIBLE
            } else {
                driverImageView.visibility = View.GONE
                GlideHelper.clear(activity, driverImageView)
            }
        }
    }

    private val serviceForTemplateObserver = Observer<Service> {
        bottomSheetContainer.setServiceForCreateTemplate(it)
    }

    private val addressForTemplateObserver = Observer<MyAddress> {
        bottomSheetContainer.setAddressForCreateTemplate(it)
    }

    private val commentForTemplateObserver = Observer<OrderDescription> {
        bottomSheetContainer.setCommentForCreateTemplate(it)
    }

    private val profileForTemplateObserver = Observer<Result<Customer>> {
        bottomSheetContainer.setProfileForCreateTemplate(it?.data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetContainer.setMapRouteManager(mapRouteManager)
        bottomSheetContainer.setMapManager(mapManager)

        bottomSheetContainer.setOnLayoutAttachListener(object: BottomSheetContainer.LayoutListener {
            override fun onOrderDetailAttached() {
                viewModel.trackingOrder.observe(viewLifecycleOwner, trackingOrderObserver)
            }

            override fun onOrderDetailDetached() {
                viewModel.trackingOrder.removeObserver(trackingOrderObserver)
            }

            override fun onRequestServiceAttached() {
                viewModel.serviceForOrder.observe(viewLifecycleOwner, serviceForOrderObserver)
                viewModel.profile.observe(viewLifecycleOwner, profileForOrderObserver)
                viewModel.addressForOrder.observe(viewLifecycleOwner, addressForOrderObserver)
                viewModel.commentForOrder.observe(viewLifecycleOwner, commentForOrderObserver)
            }

            override fun onRequestServiceDetached() {
                viewModel.serviceForOrder.removeObserver(serviceForOrderObserver)
                viewModel.profile.removeObserver(profileForOrderObserver)
                viewModel.addressForOrder.removeObserver(addressForOrderObserver)
                viewModel.commentForOrder.removeObserver(commentForOrderObserver)
            }

            override fun onCreateTemplateAttached() {
                viewModel.serviceForTemplate.observe(viewLifecycleOwner, serviceForTemplateObserver)
                viewModel.addressForTemplate.observe(viewLifecycleOwner, addressForTemplateObserver)
                viewModel.commentForTemplate.observe(viewLifecycleOwner, commentForTemplateObserver)
                viewModel.profile.observe(viewLifecycleOwner, profileForTemplateObserver)
            }

            override fun onCreateTemplateDetached() {
                viewModel.serviceForTemplate.removeObserver(serviceForTemplateObserver)
                viewModel.addressForTemplate.removeObserver(addressForTemplateObserver)
                viewModel.commentForTemplate.removeObserver(commentForTemplateObserver)
                viewModel.profile.removeObserver(profileForTemplateObserver)
            }

            override fun onTemplateListAttached() {
                viewModel.templateList.observe(viewLifecycleOwner, templateListObserver)
            }

            override fun onTemplateListDetached() {
                viewModel.templateList.removeObserver(templateListObserver)
            }
        })

        bottomSheetContainer.setSheetCallback(object : BottomSheetContainer.SheetCallback {

            override fun onDetailOrderExpanded() {
                viewModel.requestFocusOnTrackingOrderBounds()
            }

            override fun onRequestServiceExpanded() {
                viewModel.requestFocusOnOrderAddress()
            }

            override fun onDetailOrderHidden() {

            }
        })

        bottomSheetContainer.setOnRequestServiceClickListener(object: RequestServiceView.OnClickListener {

            override fun onValidatePaymentClicked() {
                viewModel.onValidatePaymentClicked()
            }

            override fun onAddressClicked() {
                viewModel.onAddressForOrderClicked()
            }

            override fun onServiceClicked() {
                viewModel.onChangeRequestingServiceClicked()
            }

            override fun onCompanyClicked() {
                viewModel.onChangeRequestingServiceCompanyClicked()
            }

            override fun onCommentClicked() {
                viewModel.onCommentForOrderClicked()
            }
        })

        bottomSheetContainer.setOnCreateTemplateClickListener(object : CreateTemplateView.OnClickListener {

            override fun onAddressClicked() {
                viewModel.onAddressForCreateTemplateClicked()
            }

            override fun onServiceClicked() {
                viewModel.onServiceForCreateTemplateClicked()
            }

            override fun onCompanyClicked() {
                viewModel.onCompanyForCreateTemplateClicked()
            }

            override fun onCommentClicked() {
                viewModel.onCommentForCreateTemplateClicked()
            }

            override fun onTimeClicked() {
                viewModel.onTimeForCreateTemplateClicked()
            }
        })

        bottomSheetContainer.setOnTemplateListClickListener(object : TemplateListView.ClickListener {
            override fun onRemoveClicked(template: ResponseTemplate) {
                viewModel.onRemoveTemplateFromListClicked(template)
            }
            override fun onCreateTemplateClicked() {
                viewModel.onOpenCreateTemplateClicked()
            }
        })

        supportMapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync(mapManager)

        myLocationFab.setOnClickListener {
            if (bottomSheetContainer.isOrderDetailVisible) {
                viewModel.trackingOrder.value?.also {
                    mapManager.animateToBounds(it.data?.driverPath?.buildLatLngBounds(), ROUTE_PADDING)
                }
            } else {
                mapManager.animateToMyLocation()
            }
        }

        addressBtn.addressTextView.setOnClickListener {
            viewModel.onAddressForOrderClicked()
        }

        requestServiceBtn.setOnClickListener {
            viewModel.onRequestServiceClicked()
        }
    }

    override fun onCameraMovedOnStart() {

    }

    override fun requestDriverMarkers(latLngBounds: LatLngBounds) {
        viewModel.requestDriverMarkers(latLngBounds)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = getViewModel(MainViewModel::class, viewModelFactory)

        viewModel.requestServiceClickHandler.observe(viewLifecycleOwner, Observer {
            ServicesListActivity.startForResult(this, RC_SERVICE_FOR_ORDER)
        })

        viewModel.changeRequestingServiceClickHandler.observe(viewLifecycleOwner, Observer {
            ServicesListActivity.startForResult(this, RC_CHANGE_SERVICE_FOR_ORDER)
        })

        viewModel.addressForOrderClickHandler.observe(viewLifecycleOwner, Observer {
            AddressSearchActivity.startForResult(this@MapFragment, it, RC_ADDRESS_FOR_ORDER)
        })

        viewModel.commentForOrderClickHandler.observe(viewLifecycleOwner, Observer {
            CommentActivity.startForResult(this, it, RC_COMMENT_FOR_ORDER)
        })

        viewModel.changeRequestingServiceCompanyClickHandler.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                CompanyListActivity.startForResult(this, RC_COMPANY_FOR_ORDER, it)
            } else {
                Toast.makeText(context, "Choose service!", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.addressForTemplateClickHandler.observe(viewLifecycleOwner, Observer {
            AddressSearchActivity.startForResult(this@MapFragment, it, RC_ADDRESS_FOR_TEMPLATE)
        })

        viewModel.serviceForTemplateClickHandler.observe(viewLifecycleOwner, Observer {
            ServicesListActivity.startForResult(this, RC_SERVICE_FOR_TEMPLATE)
        })

        viewModel.companyForTemplateClickHandler.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                CompanyListActivity.startForResult(this, RC_COMPANY_FOR_TEMPLATE, it)
            } else {
                Toast.makeText(context, "Choose service!", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.commentForTemplateClickHandler.observe(viewLifecycleOwner, Observer {
            CommentActivity.startForResult(this, it, RC_COMMENT_FOR_TEMPLATE)
        })

        viewModel.timeForTemplateClickHandler.observe(viewLifecycleOwner, Observer {
            it?.also { TemplateTimeActivity.startForResult(this@MapFragment, it, RC_TIME_FOR_TEMPLATE) }
        })

        viewModel.focusOnLatLngBoundsHandler.observe(viewLifecycleOwner, Observer {
            mapManager.animateToBounds(it, ROUTE_PADDING)
        })

        viewModel.focusOnLatLngHandler.observe(viewLifecycleOwner, Observer {
            mapManager.animateToLocation(it)
        })

        viewModel.openRequestServiceHandler.observe(viewLifecycleOwner, Observer {
            bottomSheetContainer.showRequestService()
        })

        viewModel.openCreateTemplateHandler.observe(viewLifecycleOwner, Observer {
            bottomSheetContainer.showCreateTemplate()
        })

        viewModel.templateList.observe(viewLifecycleOwner, Observer {
            val has = it?.data != null && it.data!!.size != 0
            bottomSheetContainer.setHasTemplateList(has)
        })

        viewModel.validatePayment.observe(viewLifecycleOwner, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    viewModel.requestTrackingOrder(true)
                    hideWaitingDialog()
                }
                Result.Status.ERROR -> {
                    bottomSheetContainer.showRequestService()
                    showError(it.message)
                    hideWaitingDialog()
                }
                Result.Status.LOADING -> {
                    bottomSheetContainer.hideRequestService()
                    showWaitingDialog()
                }
            }
        })

        viewModel.requestProfile()
        viewModel.requestTemplates(true)
        updateTrackingOrderHandler.post(updateTrackingOrderRunnable)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.uiSettings.isIndoorLevelPickerEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = false
        mapRouteManager.attachGoogleMap(googleMap)

        viewModel.profile.observe(viewLifecycleOwner, Observer {
            mapRouteManager.drawHomeMarker(it?.data)
        })

        viewModel.driverMarkerList.observe(viewLifecycleOwner, Observer {
            mapRouteManager.drawDriversMarkers(it)
        })

        viewModel.trackingOrder.observe(this, trackingOrderGlobalObserver)

        viewModel.addressForOrder.observe(viewLifecycleOwner, Observer {
            addressTextView.text = it?.addressString
            if(!bottomSheetContainer.isOrderDetailVisible) {
                mapManager.animateToLocation(it?.latLng)//TODO
            }

            selectedAddressMarker?.remove()
            if (it?.type == MyAddress.Type.SELECTED) {
                selectedAddressMarker = mapManager.addMarker(it.latLng, R.drawable.ic_place_black_24dp)
            }
        })
    }

    override fun onDestroyView() {
        updateTrackingOrderHandler.removeCallbacksAndMessages(null)
        viewModel.trackingOrder.removeObserver(trackingOrderGlobalObserver)
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                RC_SERVICE_FOR_ORDER -> {
                    (data?.getSerializableExtra(ServicesListActivity.EXTRA_SERVICE) as Service?)?.also {
                        viewModel.onServiceForOrderSelected(it)
                        viewModel.onOpenRequestService()
                    }
                }
                RC_CHANGE_SERVICE_FOR_ORDER -> {
                    (data?.getSerializableExtra(ServicesListActivity.EXTRA_SERVICE) as Service?)?.also {
                        viewModel.onServiceForOrderSelected(it)
                    }
                }
                RC_ADDRESS_FOR_ORDER -> {
                    data?.getParcelableExtra<MyAddress>(AddressSearchActivity.EXTRA_SELECTED_ADDRESS)?.also {
                        viewModel.setSelectedAddress(it)
                    }
                }
                RC_COMPANY_FOR_ORDER -> {
                    (data?.getSerializableExtra(CompanyListActivity.EXTRA_SERVICE) as Service?)?.also {
                        viewModel.onServiceForOrderSelected(it)
                    }
                }
                RC_COMMENT_FOR_ORDER -> {
                    (data?.getSerializableExtra(CommentActivity.EXTRA_COMMENT) as OrderDescription?)?.also {
                        viewModel.onCommentForOrderCreated(it)
                    }
                }
                RC_ADDRESS_FOR_TEMPLATE -> {
                    data?.getParcelableExtra<MyAddress>(AddressSearchActivity.EXTRA_SELECTED_ADDRESS)?.also {
                        viewModel.onAddressForTemplateSelected(it)
                    }
                }
                RC_SERVICE_FOR_TEMPLATE -> {
                    (data?.getSerializableExtra(CompanyListActivity.EXTRA_SERVICE) as Service?)?.also {
                        viewModel.onServiceForTemplateSelected(it)
                    }
                }
                RC_COMPANY_FOR_TEMPLATE -> {
                    (data?.getSerializableExtra(CompanyListActivity.EXTRA_SERVICE) as Service?)?.also {
                        viewModel.onServiceForTemplateSelected(it)
                    }
                }
                RC_COMMENT_FOR_TEMPLATE -> {
                    (data?.getSerializableExtra(CommentActivity.EXTRA_COMMENT) as OrderDescription?)?.also {
                        viewModel.onCommentForTemplateCreated(it)
                    }
                }
                RC_TIME_FOR_TEMPLATE -> {

                }
            }
        }
    }









    override fun requestPermissions() {
        requestLocationPermissions()
    }

    override fun onPermissionLocationGranted() {
        mapManager.onPermissionLocationGranted()
    }

    override fun onLocationChanged(location: Location?) {
        viewModel.onCurrentLocationChanged(location)
    }

    override fun onGpsStarted() {
//        myLocationFab.setImageResource(R.drawable.ic_my_location_black_24dp)
    }

    override fun onGpsStopped() {
//        myLocationFab.setImageResource(R.drawable.ic_location_disabled_black_24dp)
    }

    private fun showWaitingDialog() {
        var dialog = childFragmentManager.findFragmentByTag(WaitingForOrderDialogFragment.TAG) as WaitingForOrderDialogFragment?
        if (dialog == null) {
            dialog = WaitingForOrderDialogFragment.newInstance()
            dialog.show(childFragmentManager, WaitingForOrderDialogFragment.TAG)
        }
    }

    private fun hideWaitingDialog() {
        val dialog = childFragmentManager.findFragmentByTag(WaitingForOrderDialogFragment.TAG) as WaitingForOrderDialogFragment?
        dialog?.dismiss()
    }

    companion object {
        const val TAG = "MapFragment"
        private const val RC_ADDRESS_FOR_ORDER = 1
        private const val RC_SERVICE_FOR_ORDER = 2
        private const val RC_CHANGE_SERVICE_FOR_ORDER = 3
        private const val RC_COMPANY_FOR_ORDER = 4
        private const val RC_COMMENT_FOR_ORDER = 5
        private const val RC_ADDRESS_FOR_TEMPLATE = 6
        private const val RC_SERVICE_FOR_TEMPLATE = 7
        private const val RC_COMPANY_FOR_TEMPLATE = 8
        private const val RC_COMMENT_FOR_TEMPLATE = 9
        private const val RC_TIME_FOR_TEMPLATE = 10
    }
}