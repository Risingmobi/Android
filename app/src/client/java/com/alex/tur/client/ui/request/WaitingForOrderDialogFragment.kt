package com.alex.tur.client.ui.request

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import kotlinx.android.synthetic.client.fragment_dialog_waiting_for_order.*

class WaitingForOrderDialogFragment : BottomSheetDialogFragment() {

    private var listener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_waiting_for_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cancelBtn.setOnClickListener {
            listener?.onCancelClicked()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        listener = when {
            parent is Listener -> parent
            context is Listener -> context
            else -> null
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface Listener {
        fun onCancelClicked()
    }

    companion object {
        const val TAG = "WaitingForOrderDialogFragment"

        fun newInstance() = WaitingForOrderDialogFragment()

    }
}
