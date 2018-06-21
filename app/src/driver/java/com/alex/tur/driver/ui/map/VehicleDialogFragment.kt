package com.alex.tur.driver.ui.map

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.helper.Result
import com.alex.tur.model.DriverTransportMode
import kotlinx.android.synthetic.driver.dialog_fragment_vehicle.*
import timber.log.Timber

class VehicleDialogFragment : BottomSheetDialogFragment() {

    private var mListener: Listener? = null

    private lateinit var transportModeResult: Result<DriverTransportMode>

    companion object {

        private const val ARG_TRANSPORT_MODE = "ARG_TRANSPORT_MODE"
        const val TAG = "VehicleDialogFragment"

        fun newInstance(ransportModeResult: Result<DriverTransportMode>): VehicleDialogFragment =
                VehicleDialogFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_TRANSPORT_MODE, ransportModeResult)
                    }
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as Listener
        } else {
            context as Listener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        transportModeResult = (arguments?.getSerializable(ARG_TRANSPORT_MODE) as Result<DriverTransportMode>)
        setResultData(transportModeResult)

        carBtnView.setOnClickListener {
            mListener?.onTransportModeSelected(DriverTransportMode.DRIVING)
        }

        bicycleBtnView.setOnClickListener {
            mListener?.onTransportModeSelected(DriverTransportMode.BICYCLING)
        }

        walkBtnView.setOnClickListener {
            mListener?.onTransportModeSelected(DriverTransportMode.WALKING)
        }
        closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setCarEnabled() {
        carImageView.isEnabled = true
        carTextView.isEnabled = true
        bicycleImageView.isEnabled = false
        bicycleTextView.isEnabled = false
        walkImageView.isEnabled = false
        walkTextView.isEnabled = false
    }

    private fun setBicycleEnabled() {
        carImageView.isEnabled = false
        carTextView.isEnabled = false
        bicycleImageView.isEnabled = true
        bicycleTextView.isEnabled = true
        walkImageView.isEnabled = false
        walkTextView.isEnabled = false
    }

    private fun setWalkEnabled() {
        carImageView.isEnabled = false
        carTextView.isEnabled = false
        bicycleImageView.isEnabled = false
        bicycleTextView.isEnabled = false
        walkImageView.isEnabled = true
        walkTextView.isEnabled = true
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    fun setResultData(result: Result<DriverTransportMode>) {
        Timber.d("setResultData %s", result)
        transportModeResult = result
        when(result.status) {
            Result.Status.SUCCESS, Result.Status.ERROR -> {
                progressBar.visibility = View.INVISIBLE
            }
            Result.Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
        }

        selectItem(result.data)
    }

    private fun selectItem(transportMode: DriverTransportMode?) {

        when(transportMode) {
            DriverTransportMode.DRIVING -> {
                setCarEnabled()
            }
            DriverTransportMode.WALKING -> {
                setWalkEnabled()
            }
            DriverTransportMode.BICYCLING -> {
                setBicycleEnabled()
            }
            null -> {
                setNone()
            }
        }
    }

    private fun setNone() {
        carImageView.isEnabled = false
        carTextView.isEnabled = false
        bicycleImageView.isEnabled = false
        bicycleTextView.isEnabled = false
        walkImageView.isEnabled = false
        walkTextView.isEnabled = false
    }



    interface Listener {
        fun onTransportModeSelected(driverTransportMode: DriverTransportMode)
    }
}
