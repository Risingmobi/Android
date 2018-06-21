package com.alex.tur.driver.ui.map

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.alex.tur.R
import com.alex.tur.model.DriverStatus

class WorkingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var startWorkingBtn: View
    private lateinit var waitingView: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        startWorkingBtn = findViewById(R.id.startWorkingBtn)
        waitingView = findViewById(R.id.waitingView)

        startWorkingBtn.visibility = View.GONE
        waitingView.visibility = View.GONE
    }

    fun setStatus(status: DriverStatus?) {
        when(status) {
            DriverStatus.ACTIVE, DriverStatus.PENDING -> {
                startWorkingBtn.visibility = View.GONE
                waitingView.visibility = View.VISIBLE
            }
            DriverStatus.INACTIVE -> {
                startWorkingBtn.visibility = View.VISIBLE
                waitingView.visibility = View.GONE
            }
            else -> {
                startWorkingBtn.visibility = View.GONE
                waitingView.visibility = View.GONE
            }
        }
    }
}