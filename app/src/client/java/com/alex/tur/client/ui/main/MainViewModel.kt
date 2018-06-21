package com.alex.tur.client.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.location.Location
import android.os.Bundle
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.template.TemplateDataManager
import com.alex.tur.client.datamanager.drivers.DriverDataManager
import com.alex.tur.client.datamanager.order.OrderDataManager
import com.alex.tur.client.datamanager.profile.ProfileDataManager
import com.alex.tur.client.ui.address.search.AddressSearchActivity
import com.alex.tur.ext.buildLatLngBounds
import com.alex.tur.ext.fetchLatLng
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.*
import com.alex.tur.model.api.ResponseTemplate
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val profileDataManager: ProfileDataManager,
        private val orderDataManager: OrderDataManager,
        private val templateDataManager: TemplateDataManager,
        private val driverDataManager: DriverDataManager,
        private val addressFetcher: AddressFetcher
): BaseViewModel() {

    private val profileTrigger = SingleLiveEvent<Boolean>()
    val profile: LiveData<Result<Customer>> = Transformations.switchMap(profileTrigger, {
        profileDataManager.getProfile(it)
    })

    private val trackingOrderTrigger = SingleLiveEvent<Boolean>()
    val trackingOrder: LiveData<Result<Order?>> = Transformations.switchMap(trackingOrderTrigger, {
        orderDataManager.getTrackingOrder(it)
    })

    private val validatePaymentTrigger = SingleLiveEvent<RequestService>()
    val validatePayment: LiveData<Result<Order>> = Transformations.switchMap(validatePaymentTrigger, {
        orderDataManager.validatePayment(it)
    })

    private val templateListTrigger = SingleLiveEvent<Boolean>()
    val templateList: LiveData<Result<MutableList<ResponseTemplate>>> = Transformations.switchMap(templateListTrigger, {
        templateDataManager.getTemplates(it)
    })

    val driverMarkerList = MutableLiveData<MutableList<Driver>>()
    private var driversDisposable: Disposable? = null

    val requestServiceClickHandler = SingleLiveEvent<Unit>()
    val changeRequestingServiceClickHandler = SingleLiveEvent<Unit>()
    val changeRequestingServiceCompanyClickHandler = SingleLiveEvent<Service>()
    val addressForTemplateClickHandler = SingleLiveEvent<Bundle>()
    val serviceForTemplateClickHandler = SingleLiveEvent<Unit>()
    val companyForTemplateClickHandler = SingleLiveEvent<Service>()
    val timeForTemplateClickHandler = SingleLiveEvent<Template>()
    val addressForOrderClickHandler = SingleLiveEvent<Bundle>()
    val commentForTemplateClickHandler = SingleLiveEvent<OrderDescription>()
    val commentForOrderClickHandler = SingleLiveEvent<OrderDescription>()

    val focusOnLatLngBoundsHandler = SingleLiveEvent<LatLngBounds>()
    val focusOnLatLngHandler = SingleLiveEvent<LatLng>()
    val openRequestServiceHandler = SingleLiveEvent<Unit>()
    val openCreateTemplateHandler = SingleLiveEvent<Unit>()

    val serviceForOrder = MutableLiveData<Service>()
    val addressForOrder = MutableLiveData<MyAddress>()
    val commentForOrder = MutableLiveData<OrderDescription>()

    val serviceForTemplate = MutableLiveData<Service>()
    val addressForTemplate = MutableLiveData<MyAddress>()
    val commentForTemplate = MutableLiveData<OrderDescription>()

    private var addressCurrent: MyAddress? = null
    private var addressForOrderFetcherDisposable: Disposable? = null






    fun requestProfile() {
        profileTrigger.value = false
    }

    fun requestTrackingOrder(isForce: Boolean = false) {
        trackingOrderTrigger.value = isForce
    }

    fun requestDriverMarkers(latLngBounds: LatLngBounds) {
        driversDisposable = driverDataManager.loadDrivers(
                latLngBounds.southwest.latitude,
                latLngBounds.southwest.longitude,
                latLngBounds.northeast.latitude,
                latLngBounds.northeast.longitude)
                .subscribe({
                    driverMarkerList.value = it
                }, {
                    Timber.e(it)
                })
    }

    fun requestTemplates(isForce: Boolean = false) {
        templateListTrigger.value = isForce
    }







    fun onRequestServiceClicked() {
        requestServiceClickHandler.call()
    }

    fun onChangeRequestingServiceClicked() {
        changeRequestingServiceClickHandler.call()
    }

    fun onChangeRequestingServiceCompanyClicked() {
        changeRequestingServiceCompanyClickHandler.value = serviceForOrder.value
    }

    fun onAddressForOrderClicked() {
        addressForOrderClickHandler.value = Bundle().apply {
            putParcelable(AddressSearchActivity.EXTRA_CURRENT_ADDRESS, addressCurrent)
            putParcelable(AddressSearchActivity.EXTRA_HOME_ADDRESS, profile.value?.data?.let { customer ->
                customer.fetchLatLng()?.let { latLng ->
                    MyAddress.home(latLng, customer.addressString)
                }
            })
        }
    }

    fun onCommentForOrderClicked() {
        commentForOrderClickHandler.value = commentForOrder.value
    }

    fun onValidatePaymentClicked() {
        val order = RequestService()
        order.id = serviceForOrder.value!!.id
        order.service = serviceForOrder.value!!
        order.address = addressForOrder.value!!.addressString
        order.lat = addressForOrder.value!!.latLng.latitude
        order.lng = addressForOrder.value!!.latLng.longitude
        order.status = OrderStatus.ACTIVE
        order.paymentStatus = PaymentStatus.INVOICE
        order.requestDescription = OrderDescription().apply {
            brieflyDescription = commentForOrder.value?.brieflyDescription
            picture = commentForOrder.value?.picture
        }

        validatePaymentTrigger.value = order
    }

    fun onAddressForCreateTemplateClicked() {
        addressForTemplateClickHandler.value = Bundle().apply {
            putParcelable(AddressSearchActivity.EXTRA_CURRENT_ADDRESS, addressCurrent)
            putParcelable(AddressSearchActivity.EXTRA_HOME_ADDRESS, profile.value?.data?.let { customer ->
                customer.fetchLatLng()?.let { latLng ->
                    MyAddress.home(latLng, customer.addressString)
                }
            })
        }
    }

    fun onServiceForCreateTemplateClicked() {
        serviceForTemplateClickHandler.call()
    }

    fun onCompanyForCreateTemplateClicked() {
        companyForTemplateClickHandler.value = serviceForTemplate.value
    }

    fun onCommentForCreateTemplateClicked() {
        commentForTemplateClickHandler.value = commentForTemplate.value
    }

    fun onTimeForCreateTemplateClicked() {
        val template = Template()
        template.requestDescription = commentForTemplate.value
        template.lat = addressForTemplate.value?.latLng?.latitude
        template.lng = addressForTemplate.value?.latLng?.longitude
        template.serviceDescription = Template.ServiceDescription().apply { id = serviceForTemplate.value?.id }
        timeForTemplateClickHandler.value = template
    }

    fun onRemoveTemplateFromListClicked(template: ResponseTemplate) {

    }

    fun requestFocusOnTrackingOrderBounds() {
        focusOnLatLngBoundsHandler.value = trackingOrder.value?.let {
            it.data?.driverPath?.buildLatLngBounds()
        }
    }

    fun requestFocusOnOrderAddress() {
        focusOnLatLngHandler.value = addressForOrder.value?.latLng
    }




    fun onOpenRequestService() {
        openRequestServiceHandler.call()
    }

    fun onServiceForOrderSelected(service: Service) {
        serviceForOrder.value = service
    }

    fun onCommentForOrderCreated(orderDescription: OrderDescription) {
        commentForOrder.value = orderDescription
    }

    fun onServiceForTemplateSelected(service: Service) {
        serviceForTemplate.value = service
    }

    fun onAddressForTemplateSelected(address: MyAddress) {
        addressForTemplate.value = address
    }

    fun onCommentForTemplateCreated(orderDescription: OrderDescription) {
        commentForTemplate.value = orderDescription
    }

    fun onOpenCreateTemplateClicked() {
        openCreateTemplateHandler.call()
    }





    fun onCurrentLocationChanged(location: Location?) {
        location?.also {
            addressCurrent = MyAddress.current(it)
            if (addressForOrder.value == null) {
                setSelectedAddress(MyAddress.current(it))
            }
        }
    }

    fun setSelectedAddress(address: MyAddress) {
        addressForOrderFetcherDisposable?.dispose()
        addressForOrderFetcherDisposable = addressFetcher.fetchAddressAsync(address.latLng, address.type)
                .subscribe({
                    addressForOrder.setValue(it)
                }, {
                    Timber.e(it)
                })
    }

    override fun onDestroyView() {
        addressForOrderFetcherDisposable?.dispose()
        driversDisposable?.dispose()
    }


}