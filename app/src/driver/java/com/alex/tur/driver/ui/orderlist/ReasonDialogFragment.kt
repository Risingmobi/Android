package com.alex.tur.driver.ui.orderlist

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.AppConstants.INVALID_ID
import com.alex.tur.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.driver.fragment_reason_dialog.*
import timber.log.Timber

private const val ARG_ORDER_ID = "ARG_ORDER_ID"

class ReasonDialogFragment : DialogFragment() {

    private var orderId: Int = INVALID_ID

    private var listener: OnReasonListener? = null

    private var textChangeDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments!!.getInt(ARG_ORDER_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reason_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dismissBtn.setOnClickListener {
            dismiss()
        }

        textChangeDisposable = RxTextView.textChanges(editText)
                .subscribe({
                    sendBtn.isEnabled = !it.isNullOrBlank()
                }, {
                    Timber.e(it)
                })

        sendBtn.setOnClickListener {
            done()
        }
    }

    private fun done() {
        val reason = editText.text.toString()
        listener?.onReasonInteraction(orderId, reason)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            context is OnReasonListener -> context
            parentFragment is OnReasonListener -> parentFragment as OnReasonListener
            else -> throw RuntimeException(context.toString() + " must implement OnReasonListener")
        }
    }

    override fun onStart() {
        super.onStart()

        val window = dialog.window
        val display = window!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        window.setLayout((width * 0.9f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        textChangeDisposable?.dispose()
    }

    interface OnReasonListener {
        fun onReasonInteraction(orderId: Int, reason: String)
    }

    companion object {

        const val TAG = "ReasonDialogFragment"

        @JvmStatic
        fun newInstance(orderId: Int) =
                ReasonDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ORDER_ID, orderId)
                    }
                }
    }
}
