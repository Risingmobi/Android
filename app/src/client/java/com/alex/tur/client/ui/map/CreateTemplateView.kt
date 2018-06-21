package com.alex.tur.client.ui.map

import android.content.Context
import android.graphics.Color
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
import com.alex.tur.utils.DimensUtils
import kotlinx.android.synthetic.client.create_template.view.*
import kotlinx.android.synthetic.client.item_action.view.*
import timber.log.Timber

class CreateTemplateView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private var bottomSheetBehavior: MyBottomSheetBehavior<View>

    private var callback: Callback? = null
    private var clickListener: OnClickListener? = null
    private var layoutListener: LayoutListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.create_template, this)

        bottomSheetBehavior = MyBottomSheetBehavior.from(bottomSheetLayout, "CreateTemplateView")
        bottomSheetBehavior.peekHeight = DimensUtils.dpToPx(50f)
        bottomSheetBehavior.setBottomSheetCallback(object : MyBottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                dispatchOnSlide(bottomSheet, slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                dispatchStateChanged(bottomSheet, newState)
            }
        })
        hide()

        bottomSheetLayout.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (bottomSheetLayout.height != 0) {
                    bottomSheetLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    dispatchOnSlide(bottomSheetLayout, bottomSheetBehavior.slideOffset)
                    dispatchStateChanged(bottomSheetLayout, bottomSheetBehavior.state)
                }
            }
        })

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

        chooseTimeBtn.setOnClickListener {
            clickListener?.onTimeClicked()
        }

        initViews()
    }

    private fun initViews() {
        cancelBtn.alpha = 0f

        serviceView.icon.imageTintList = null
        companyView.icon.imageTintList = null

        BitmapUtils.setTint(context, commentView.icon, R.color.colorAccent)

        BitmapUtils.setColorFilter(context, commentView.icon, R.color.colorAccent)
        commentView.paramTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        commentView.arrowImageView.visibility = View.GONE
        payCardView.icon.setImageResource(R.drawable.ic_payment_black_24dp)

        setProfile(null)
        setAddress(null)
        setService(null)
        setComment(null)
    }

    fun setHideable(isHideable: Boolean) {
        bottomSheetBehavior.isHideable = isHideable
    }






    private fun dispatchOnSlide(bottomSheet: View, slideOffset: Float) {
        val offset: Float = if (slideOffset > 0) {
            slideOffset
        } else {
            0f
        }
        setBackgroundColor(Color.argb((offset * 200).toInt(),0,0,0))
        callback?.onCreateTemplateBottomSheetSlide(bottomSheet, slideOffset)

    }

    private fun dispatchStateChanged(bottomSheet: View, newState: Int) {
        when(newState) {
            MyBottomSheetBehavior.STATE_EXPANDED -> {
                cancelBtn.animate().alpha(1f).setDuration(150)
            }
            MyBottomSheetBehavior.STATE_COLLAPSED -> {
                cancelBtn.animate().alpha(0f).setDuration(150)
            }
        }
        when(newState) {
            MyBottomSheetBehavior.STATE_EXPANDED, MyBottomSheetBehavior.STATE_COLLAPSED -> {
                setHideable(false)
            }
        }
        callback?.onCreateTemplateBottomSheetStateChanged(bottomSheet, newState)
    }





    fun setProfile(data: Customer?) {
        data?.also { customer ->
            payCardView.paramTextView.text = "Invoice"//TODO
        }
    }

    fun setAddress(address: MyAddress?) {
        addressView.paramTextView.text = address?.addressString ?: "Address"
        when(address?.type) {
            MyAddress.Type.HOME -> {addressView.icon.setImageResource(R.drawable.ic_home_black_24dp)}
            MyAddress.Type.CURRENT -> {addressView.icon.setImageResource(R.drawable.ic_my_location_black_24dp)}
            MyAddress.Type.SELECTED -> {addressView.icon.setImageResource(R.drawable.ic_place_black_24dp)}
            MyAddress.Type.NONE, null -> {addressView.icon.setImageResource(R.drawable.ic_place_black_24dp)}
        }
    }

    fun setService(service: Service?) {
        Timber.d("setService $service")
        if (service != null) {
            BitmapUtils.clearColorFilter(context, serviceView.icon)
            BitmapUtils.clearColorFilter(context, companyView.icon)
            serviceView.paramTextView.text = service.naming
            companyView.paramTextView.text = service.company?.naming
            priceTextView.text = "$${service.company?.cost}"
            GlideHelper.load(context, serviceView.icon, service.picture)
            GlideHelper.load(context, companyView.icon, service.company?.picture)
        } else {
            BitmapUtils.setColorFilter(context, serviceView.icon, R.color.colorIcon)
            BitmapUtils.setColorFilter(context, companyView.icon, R.color.colorIcon)
            serviceView.paramTextView.text = "Choose service"
            companyView.paramTextView.text = "Choose company"
            priceTextView.text = ""
            GlideHelper.load(context, serviceView.icon, R.drawable.ic_request_service)
            GlideHelper.load(context, companyView.icon, R.drawable.ic_request_service)
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





    fun expand() {
        bottomSheetBehavior.state = MyBottomSheetBehavior.STATE_EXPANDED
    }

    fun collapse() {
        bottomSheetBehavior.state = MyBottomSheetBehavior.STATE_COLLAPSED
    }

    fun hide() {
        setHideable(true)
        bottomSheetBehavior.state = MyBottomSheetBehavior.STATE_HIDDEN
    }





    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setClickListener(listener: OnClickListener) {
        clickListener = listener
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
        fun onAddressClicked()
        fun onServiceClicked()
        fun onCommentClicked()
        fun onTimeClicked()
        fun onCompanyClicked()
    }

    interface Callback {
        fun onCreateTemplateBottomSheetSlide(bottomSheet: View, slideOffset: Float)
        fun onCreateTemplateBottomSheetStateChanged(bottomSheet: View, newState: Int)
    }

    interface LayoutListener {
        fun onDetached()
        fun onAttached()
    }
}