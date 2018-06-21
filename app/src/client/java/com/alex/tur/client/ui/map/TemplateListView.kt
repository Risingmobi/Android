package com.alex.tur.client.ui.map

import android.content.Context
import android.graphics.Color
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import com.alex.tur.R
import com.alex.tur.client.ui.template.TemplateDelegateAdapter
import com.alex.tur.helper.MyBottomSheetBehavior
import com.alex.tur.helper.Result
import com.alex.tur.model.Template
import com.alex.tur.model.api.ResponseTemplate
import com.alex.tur.recycleradapter.SimpleCompositeAdapter
import com.alex.tur.utils.DimensUtils
import kotlinx.android.synthetic.client.template_list.view.*
import timber.log.Timber

class TemplateListView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private var bottomSheetBehavior: MyBottomSheetBehavior<View>

    private var callback: Callback? = null

    private var layoutListener: LayoutListener? = null

    private var clickListener: ClickListener? = null

    private var adapter: SimpleCompositeAdapter

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setLayoutListener(listener: LayoutListener?) {
        this.layoutListener = listener
    }
    fun setOnClickListener(listener: ClickListener) {
        clickListener = listener
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.template_list, this)

        bottomSheetBehavior = MyBottomSheetBehavior.from(bottomSheetLayout, "TemplateListView")
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

        adapter = SimpleCompositeAdapter.Builder()
                .add(TemplateDelegateAdapter().apply {
                    setOnRemoveItemClickListener {
                        clickListener?.onRemoveClicked(it)
                    }
                })
                .build()

        fab.setOnClickListener {
            clickListener?.onCreateTemplateClicked()
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
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
        peekView.alpha = 1 - slideOffset
        callback?.onTemplateListBottomSheetSlide(bottomSheet, slideOffset)
    }

    private fun dispatchStateChanged(bottomSheet: View, newState: Int) {
        when(newState) {
            MyBottomSheetBehavior.STATE_EXPANDED -> {
                peekView.visibility = View.GONE
            }
            else -> {
                if (peekView.visibility == View.GONE) {
                    peekView.visibility = View.VISIBLE
                }
            }
        }
        when(newState) {
            MyBottomSheetBehavior.STATE_EXPANDED, MyBottomSheetBehavior.STATE_COLLAPSED -> {
                setHideable(false)
            }
        }
        callback?.onTemplateListBottomSheetStateChanged(bottomSheet, newState)
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

    fun setData(result: Result<MutableList<ResponseTemplate>>?) {
        result?.data?.also {
            if(it.size != 0) {
                adapter.swapData(it)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutListener?.onAttached()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        layoutListener?.onDetached()
        callback = null
        layoutListener = null
    }

    interface Callback {
        fun onTemplateListBottomSheetSlide(bottomSheet: View, slideOffset: Float)
        fun onTemplateListBottomSheetStateChanged(bottomSheet: View, newState: Int)
    }

    interface ClickListener {
        fun onRemoveClicked(template: ResponseTemplate)
        fun onCreateTemplateClicked()
    }

    interface LayoutListener {
        fun onDetached()
        fun onAttached()
    }
}