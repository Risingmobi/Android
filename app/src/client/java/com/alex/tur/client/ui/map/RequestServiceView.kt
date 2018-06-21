package com.alex.tur.client.ui.map

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import com.alex.tur.R
import com.alex.tur.helper.GlideHelper
import com.alex.tur.helper.MyBottomSheetBehavior
import com.alex.tur.model.Customer
import com.alex.tur.model.MyAddress
import com.alex.tur.model.OrderDescription
import com.alex.tur.model.Service
import com.alex.tur.utils.BitmapUtils
import kotlinx.android.synthetic.client.item_action.view.*
import kotlinx.android.synthetic.client.request_service.view.*

class RequestServiceView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private var bottomSheetBehavior: MyBottomSheetBehavior<View>

    private var callback: Callback? = null
    private var clickListener: OnClickListener? = null
    private var layoutListener: LayoutListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.request_service, this)
        bottomSheetLayout.isClickable = true
        bottomSheetBehavior = MyBottomSheetBehavior.from(bottomSheetLayout, "RequestServiceView")
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.setBottomSheetCallback(object : MyBottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                callback?.onRequestServiceBottomSheetSlide(bottomSheet, slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                callback?.onRequestServiceBottomSheetStateChanged(bottomSheet, newState)
            }
        })
        hide()


        bottomSheetLayout.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (bottomSheetLayout.height != 0) {
                    bottomSheetLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback?.onRequestServiceBottomSheetStateChanged(bottomSheetLayout, bottomSheetBehavior.state)
                    callback?.onRequestServiceBottomSheetSlide(bottomSheetLayout, bottomSheetBehavior.slideOffset)
                }
            }
        })

        validatePaymentBtn.setOnClickListener {
            clickListener?.onValidatePaymentClicked()
        }

        addressView.setOnClickListener {
            clickListener?.onAddressClicked()
        }

        serviceView.setOnClickListener {
            clickListener?.onServiceClicked()
        }

        companyView.setOnClickListener {
            clickListener?.onCompanyClicked()
        }

        commentView.setOnClickListener {
            clickListener?.onCommentClicked()
        }

        initViews()
    }

    fun setService(service: Service?) {
        serviceView.paramTextView.text = service?.naming
        GlideHelper.load(context, serviceView.icon, service?.picture)
        companyView.paramTextView.text = service?.company?.naming
        GlideHelper.load(context, companyView.icon, service?.company?.picture)
        priceTextView.text = "$${service?.company?.cost}"
    }

    fun setProfile(data: Customer?) {
        data?.also { customer ->
            payCardView.paramTextView.text = "Invoice"//TODO
        }
    }

    fun setComment(comment: OrderDescription?) {
        if (comment != null && (!comment.brieflyDescription.isNullOrBlank() || !comment.picture.isNullOrBlank())) {
            if (!comment.brieflyDescription.isNullOrBlank()) {
                commentView.paramTextView.text = comment.brieflyDescription
            } else {
                commentView.paramTextView.text = "Image"
            }
            commentView.icon.setImageResource(R.drawable.ic_attachment_black_24dp)
        } else {
            commentView.paramTextView.text = "Add comment"
            commentView.icon.setImageResource(R.drawable.ic_add_black_24dp)
        }
    }

    fun setAddress(address: MyAddress?) {
        addressView.paramTextView.text = address?.addressString
        when(address?.type) {
            MyAddress.Type.HOME -> {addressView.icon.setImageResource(R.drawable.ic_home_black_24dp)}
            MyAddress.Type.CURRENT -> {addressView.icon.setImageResource(R.drawable.ic_my_location_black_24dp)}
            MyAddress.Type.SELECTED -> {addressView.icon.setImageResource(R.drawable.ic_place_black_24dp)}
            MyAddress.Type.NONE, null -> {addressView.icon.setImageResource(R.drawable.ic_place_black_24dp)}
        }
    }

    private fun initViews() {
        addressView.paramTextView.text = ""
        serviceView.paramTextView.text = ""
        companyView.paramTextView.text = ""
        commentView.paramTextView.text = "Add comment"
        payCardView.paramTextView.text = ""

        serviceView.icon.imageTintList = null
        companyView.icon.imageTintList = null

        BitmapUtils.setTint(context, commentView.icon, R.color.colorAccent)

        commentView.icon.setImageResource(R.drawable.ic_add_black_24dp)
        commentView.paramTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        commentView.arrowImageView.visibility = View.GONE
        payCardView.icon.setImageResource(R.drawable.ic_payment_black_24dp)
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

    fun setClickListener(listener: OnClickListener?) {
        this.clickListener = listener
    }

    fun setLayoutListener(listener: LayoutListener?) {
        this.layoutListener = listener
    }





    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutListener?.onAttached()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        layoutListener?.onDetached()
        callback = null
        clickListener = null
        layoutListener = null
    }




    interface OnClickListener {
        fun onValidatePaymentClicked()
        fun onAddressClicked()
        fun onServiceClicked()
        fun onCompanyClicked()
        fun onCommentClicked()
    }

    interface Callback {
        fun onRequestServiceBottomSheetSlide(bottomSheet: View, slideOffset: Float)
        fun onRequestServiceBottomSheetStateChanged(bottomSheet: View, newState: Int)
    }

    interface LayoutListener {
        fun onDetached()
        fun onAttached()
    }
}