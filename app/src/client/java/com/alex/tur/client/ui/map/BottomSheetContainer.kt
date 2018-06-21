package com.alex.tur.client.ui.map

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alex.tur.R
import com.alex.tur.base.MapManager
import com.alex.tur.client.datamanager.MapRouteManager
import com.alex.tur.helper.MyBottomSheetBehavior
import com.alex.tur.helper.Result
import com.alex.tur.model.*
import com.alex.tur.model.api.ResponseTemplate
import com.alex.tur.utils.DimensUtils
import kotlinx.android.synthetic.client.fragment_map.view.*

class BottomSheetContainer @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CreateTemplateView.Callback, OrderDetailView.Callback, RequestServiceView.Callback, TemplateListView.Callback {

    var isOrderDetailVisible = false
    private var hasTrackingOrder = false

    private lateinit var mapRouteManager: MapRouteManager
    private lateinit var mapManager: MapManager

    private var orderDetailView: OrderDetailView? = null
    private var requestServiceView: RequestServiceView? = null
    private var createTemplateView: CreateTemplateView? = null
    private var templateListView: TemplateListView? = null

    private var requestServiceClickListener: RequestServiceView.OnClickListener? = null
    private var createTemplateClickListener: CreateTemplateView.OnClickListener? = null
    private var templateListClickListener: TemplateListView.ClickListener? = null
    private var sheetCallback: SheetCallback? = null
    private var layoutListener: LayoutListener? = null

    init {
        orderDetailView = OrderDetailView(context).apply {
            setCallback(this@BottomSheetContainer)
            layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                bottomMargin = DimensUtils.dpToPx(50f)
            }
            findViewById<View>(R.id.hideBtn).setOnClickListener { orderDetailView?.hide() }
            setLayoutListener(object: OrderDetailView.LayoutListener {
                override fun onAttached() {
                    layoutListener?.onOrderDetailAttached()
                }

                override fun onDetached() {
                    layoutListener?.onOrderDetailDetached()
                }
            })
        }
        requestServiceView = RequestServiceView(context).apply {
            setCallback(this@BottomSheetContainer)
            layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                bottomMargin = DimensUtils.dpToPx(50f)
            }
            setClickListener(object : RequestServiceView.OnClickListener {

                override fun onValidatePaymentClicked() {
                    requestServiceClickListener?.onValidatePaymentClicked()
                }

                override fun onAddressClicked() {
                    requestServiceClickListener?.onAddressClicked()
                }

                override fun onServiceClicked() {
                    requestServiceClickListener?.onServiceClicked()
                }

                override fun onCompanyClicked() {
                    requestServiceClickListener?.onCompanyClicked()
                }

                override fun onCommentClicked() {
                    requestServiceClickListener?.onCommentClicked()
                }
            })
            setLayoutListener(object: RequestServiceView.LayoutListener {
                override fun onAttached() {
                    layoutListener?.onRequestServiceAttached()
                }

                override fun onDetached() {
                    layoutListener?.onRequestServiceDetached()
                }
            })
        }
        createTemplateView = CreateTemplateView(context).apply {
            setCallback(this@BottomSheetContainer)
            layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            }
            setClickListener(object : CreateTemplateView.OnClickListener {

                override fun onAddressClicked() {
                    createTemplateClickListener?.onAddressClicked()
                }

                override fun onServiceClicked() {
                    createTemplateClickListener?.onServiceClicked()
                }

                override fun onCompanyClicked() {
                    createTemplateClickListener?.onCompanyClicked()
                }

                override fun onCommentClicked() {
                    createTemplateClickListener?.onCommentClicked()
                }

                override fun onTimeClicked() {
                    createTemplateClickListener?.onTimeClicked()
                }
            })
            setLayoutListener(object: CreateTemplateView.LayoutListener {
                override fun onAttached() {
                    layoutListener?.onCreateTemplateAttached()
                }

                override fun onDetached() {
                    layoutListener?.onCreateTemplateDetached()
                }
            })
            findViewById<View>(R.id.peekView).setOnClickListener { expand() }
            findViewById<View>(R.id.cancelBtn).setOnClickListener { collapse() }
        }

        templateListView = TemplateListView(context).apply {
            setCallback(this@BottomSheetContainer)
            setLayoutListener(object: TemplateListView.LayoutListener {
                override fun onAttached() {
                    layoutListener?.onTemplateListAttached()
                }

                override fun onDetached() {
                    layoutListener?.onTemplateListDetached()
                }
            })
            setOnClickListener(object : TemplateListView.ClickListener {
                override fun onRemoveClicked(template: ResponseTemplate) {
                    templateListClickListener?.onRemoveClicked(template)
                }
                override fun onCreateTemplateClicked() {
                    templateListClickListener?.onCreateTemplateClicked()
                }
            })
            findViewById<View>(R.id.peekView).setOnClickListener { expand() }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Handler().post {
            val sheetContainer = FrameLayout(context)
            addView(sheetContainer)
            sheetContainer.addView(orderDetailView)
            sheetContainer.addView(requestServiceView)
            sheetContainer.addView(createTemplateView)
            sheetContainer.addView(templateListView)
        }

        driverImageView.visibility = View.GONE
        driverImageView.setOnClickListener {
            isOrderDetailVisible = true
            mapRouteManager.drawDriverRoute(true)
            showDetailOrder()
        }
    }

    fun setMapRouteManager(mapRouteManager: MapRouteManager) {
        this.mapRouteManager = mapRouteManager
    }

    fun setMapManager(mapManager: MapManager) {
        this.mapManager = mapManager
    }

    override fun onRequestServiceBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        onBottomSheetSlide(bottomSheet, slideOffset)
        if (slideOffset < 0.3) {
            val alpha = 1 - ((slideOffset) * Math.tan(Math.atan(1 / 0.3))).toFloat()
            addressBtn.alpha = alpha
            requestServiceBtn.alpha = alpha
            if (hasTrackingOrder && !isOrderDetailVisible) {
                driverImageView.alpha = alpha
            }
        } else {
            addressBtn.alpha = 0f
            requestServiceBtn.alpha = 0f
            if (hasTrackingOrder && !isOrderDetailVisible) {
                driverImageView.alpha = 0f
            }
        }
    }

    override fun onDetailOrderBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        onBottomSheetSlide(bottomSheet, slideOffset)
        if (slideOffset < 0.3) {
            val alpha = 1 - ((slideOffset) * Math.tan(Math.atan(1 / 0.3))).toFloat()
            addressBtn.alpha = alpha
            requestServiceBtn.alpha = alpha
            driverImageView.alpha = alpha
        } else {
            addressBtn.alpha = 0f
            requestServiceBtn.alpha = 0f
            driverImageView.alpha = 0f
        }
    }

    override fun onTemplateListBottomSheetSlide(bottomSheet: View, slideOffset: Float) {

    }

    override fun onCreateTemplateBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
    }







    override fun onRequestServiceBottomSheetStateChanged(bottomSheet: View, newState: Int) {
        onBottomSheetStateChanged(bottomSheet, newState)
        when(newState) {
            MyBottomSheetBehavior.STATE_EXPANDED -> {
                sheetCallback?.onRequestServiceExpanded()
                if (hasTrackingOrder && !isOrderDetailVisible) {
                    driverImageView.visibility = View.GONE
                }
            }
            MyBottomSheetBehavior.STATE_HIDDEN -> {
                if (hasTrackingOrder && !isOrderDetailVisible) {
                    driverImageView.visibility = View.VISIBLE
                }
            }
            else -> {
                if (hasTrackingOrder && !isOrderDetailVisible) {
                    driverImageView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDetailOrderBottomSheetStateChanged(bottomSheet: View, newState: Int) {
        onBottomSheetStateChanged(bottomSheet, newState)
        when(newState) {
            MyBottomSheetBehavior.STATE_EXPANDED -> {
                isOrderDetailVisible = true
                sheetCallback?.onDetailOrderExpanded()
                driverImageView.visibility = View.GONE
            }
            MyBottomSheetBehavior.STATE_HIDDEN -> {
                sheetCallback?.onDetailOrderHidden()
                mapRouteManager.drawDriverRoute(false)
                isOrderDetailVisible = false
                driverImageView.visibility = View.VISIBLE
            }
            else -> {
                if (hasTrackingOrder) {
                    driverImageView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onTemplateListBottomSheetStateChanged(bottomSheet: View, newState: Int) {

    }

    override fun onCreateTemplateBottomSheetStateChanged(bottomSheet: View, newState: Int) {

    }







    fun onBottomSheetStateChanged(bottomSheet: View, newState: Int) {
        when(newState) {
            MyBottomSheetBehavior.STATE_EXPANDED -> {
                addressBtn.visibility = View.GONE
                requestServiceBtn.isEnabled = false
            }
            MyBottomSheetBehavior.STATE_HIDDEN -> {
                addressBtn.visibility = View.VISIBLE
                requestServiceBtn.isEnabled = true
            }
            else -> {
                addressBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun onBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        if (bottomSheet.top < (myLocationFab.bottom + myLocationFab.paddingBottom)) {
            myLocationFab.translationY = (bottomSheet.top - myLocationFab.bottom - myLocationFab.paddingBottom).toFloat()
        } else {
            myLocationFab.translationY = 0f
        }

        if (bottomSheet.top < bottom) {
            mapManager.googleMap?.setPadding(0,0,0,(bottom - bottomSheet.top))
        } else {
            mapManager.googleMap?.setPadding(0,0,0,0)
        }
    }









    fun setTemplateListData(result: Result<MutableList<ResponseTemplate>>?) {
        templateListView?.setData(result)
    }

    fun setAddressForCreateTemplate(address: MyAddress?) {
        createTemplateView?.setAddress(address)
    }

    fun setServiceForCreateTemplate(service: Service?) {
        createTemplateView?.setService(service)
    }

    fun setCommentForRequestService(comment: OrderDescription?) {
        requestServiceView?.setComment(comment)
    }

    fun setCommentForCreateTemplate(comment: OrderDescription?) {
        createTemplateView?.setComment(comment)
    }

    fun setProfileForCreateTemplate(customer: Customer?) {
        createTemplateView?.setProfile(customer)
    }

    fun setServiceForRequestService(service: Service?) {
        requestServiceView?.setService(service)
    }

    fun setProfileForRequestService(customer: Customer?) {
        requestServiceView?.setProfile(customer)
    }

    fun setAddressForRequestService(address: MyAddress?) {
        requestServiceView?.setAddress(address)
    }

    fun attachTrackingOrderData(result: Result<Order?>?) {
        hasTrackingOrder = result?.data != null
        mapRouteManager.drawDriverRoute(result?.data, isOrderDetailVisible)
        orderDetailView?.attachData(result?.data)
        if (result?.data == null) {
            hideDetailOrder()
        }
    }






    var count = 0

    fun setHasTemplateList(has: Boolean) {
        //TODO create and add "template list" or "create template" bottom sheet
//        Timber.d("setHasTemplateList $has")
        if (has) {
            showTemplateList()
        } else {
            showPeekCreateTemplate()
        }
        count++
    }

    fun setHasTrackingOrder(has: Boolean) {
        //TODO create and add "order detail" bottom sheet or not
        hasTrackingOrder = has
    }






    fun showRequestService() {
        requestServiceView?.show()
        hideDetailOrder()
    }

    fun hideRequestService() {
        requestServiceView?.hide()
    }

    fun showDetailOrder() {
        orderDetailView?.show()
        hideRequestService()
    }

    fun hideDetailOrder() {
        orderDetailView?.hide()
    }

    fun showPeekCreateTemplate() {
        createTemplateView?.collapse()
        hideTemplateList()
    }

    fun showCreateTemplate() {
        createTemplateView?.expand()
        hideTemplateList()
    }

    fun hideCreateTemplate() {
        createTemplateView?.hide()
    }

    fun showTemplateList() {
        templateListView?.collapse()
        hideCreateTemplate()
    }

    fun hideTemplateList() {
        templateListView?.hide()
    }






    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sheetCallback = null
        createTemplateClickListener = null
        requestServiceClickListener = null
        templateListClickListener = null
        layoutListener = null
    }

    fun setSheetCallback(callback: SheetCallback) {
        this.sheetCallback = callback
    }

    fun setOnRequestServiceClickListener(listener: RequestServiceView.OnClickListener) {
        requestServiceClickListener = listener
    }

    fun setOnCreateTemplateClickListener(listener: CreateTemplateView.OnClickListener) {
        createTemplateClickListener = listener
    }

    fun setOnTemplateListClickListener(listener: TemplateListView.ClickListener) {
        templateListClickListener = listener
    }

    fun setOnLayoutAttachListener(listener: LayoutListener) {
        this.layoutListener = listener
    }



    interface SheetCallback {
        fun onDetailOrderExpanded()
        fun onRequestServiceExpanded()
        fun onDetailOrderHidden()
    }

    interface LayoutListener {
        fun onOrderDetailAttached()
        fun onOrderDetailDetached()
        fun onRequestServiceAttached()
        fun onRequestServiceDetached()
        fun onCreateTemplateAttached()
        fun onCreateTemplateDetached()
        fun onTemplateListAttached()
        fun onTemplateListDetached()
    }
}