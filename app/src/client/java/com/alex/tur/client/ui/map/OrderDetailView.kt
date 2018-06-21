package com.alex.tur.client.ui.map

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import com.alex.tur.R
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.GlideHelper
import com.alex.tur.helper.MyBottomSheetBehavior
import com.alex.tur.model.MyAddress
import com.alex.tur.model.Order
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.client.order_detail.view.*
import timber.log.Timber

class OrderDetailView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private val addressFetcher = AddressFetcher(context)
    private var addressFetcherDisposable: Disposable? = null

    private var bottomSheetBehavior: MyBottomSheetBehavior<View>

    private var callback: Callback? = null
    private var layoutListener: LayoutListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.order_detail, this)
        bottomSheetLayout.isClickable = true
        bottomSheetBehavior = MyBottomSheetBehavior.from(bottomSheetLayout, "OrderDetailView")
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.setBottomSheetCallback(object : MyBottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                callback?.onDetailOrderBottomSheetSlide(bottomSheet, slideOffset)
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                callback?.onDetailOrderBottomSheetStateChanged(bottomSheet, newState)
            }
        })
        hide()


        bottomSheetLayout.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (bottomSheetLayout.height != 0) {
                    bottomSheetLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback?.onDetailOrderBottomSheetStateChanged(bottomSheetLayout, bottomSheetBehavior.state)
                    callback?.onDetailOrderBottomSheetSlide(bottomSheetLayout, bottomSheetBehavior.slideOffset)
                }
            }
        })
    }

    fun show() {
        bottomSheetBehavior.state = MyBottomSheetBehavior.STATE_EXPANDED
    }

    fun hide() {
        bottomSheetBehavior.state = MyBottomSheetBehavior.STATE_HIDDEN
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun setLayoutListener(listener: LayoutListener?) {
        this.layoutListener = listener
    }

    fun attachData(data: Order?) {
        Timber.d("attachData $data")
        data?.also { order ->
            GlideHelper.loadAvatar(context, driverAvatar, order.willBeEvaluatedBy?.avatar)
            nameTextView.text = order.willBeEvaluatedBy?.name
            carTextView.text = "${order.willBeEvaluatedBy?.vehicleModel} ${order.willBeEvaluatedBy?.vehicleNumber}"
            GlideHelper.load(context, companyImageView, order.service?.company?.picture)
            companyTextView.text = order.service?.company?.naming
            fetchAddress(order.lat, order.lng)
            serviceTextView.text = order.service?.naming
            paymentTextView.text = order.paymentStatus?.toString()
            timeTextView.text = "The service will be done in ${order.estimationDurationAndDistance?.duration?.text}"
        }
    }

    private fun fetchAddress(lat: Double?, lng: Double?) {
        addressFetcherDisposable?.dispose()
        addressFetcherDisposable = addressFetcher.fetchAddressAsync(lat, lng, MyAddress.Type.NONE)
                .subscribe({
                    addressText.text = it?.addressString
                }, {
                    Timber.e(it)
                })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutListener?.onAttached()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        layoutListener?.onDetached()
        addressFetcherDisposable?.dispose()
        callback = null
        layoutListener = null
    }

    interface Callback {
        fun onDetailOrderBottomSheetSlide(bottomSheet: View, slideOffset: Float)
        fun onDetailOrderBottomSheetStateChanged(bottomSheet: View, newState: Int)
    }

    interface LayoutListener {
        fun onDetached()
        fun onAttached()
    }
}