package com.alex.tur.driver.ui.profile.edit_car

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.Result
import kotlinx.android.synthetic.driver.activity_edit_car.*
import kotlinx.android.synthetic.driver.edit_vehicle_model.*
import kotlinx.android.synthetic.driver.edit_vehicle_number.*
import java.io.Serializable
import javax.inject.Inject

class EditCarActivity : BaseActivity() {

    lateinit var viewModel: EditCarViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<EditCarViewModel>

    @Inject
    lateinit var carInfo: CarInfo

    private var numberContainer: View? = null

    private var modelContainer: View? = null

    companion object {

        const val EXTRA_CAR = "EXTRA_CAR"

        fun start(activity: Activity?, carInfo: CarInfo) {
            activity?.startActivity(Intent(activity, EditCarActivity::class.java).apply {
                putExtra(EXTRA_CAR, carInfo)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_car)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        viewPager.adapter = Adapter()
        tabs.setupWithViewPager(viewPager)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditCarViewModel::class.java)

        viewModel.saveAction.observe(this, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    finish()
                }
                Result.Status.ERROR -> {
                    hideLoading()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
            }
        })

        saveBtn.setOnClickListener {
            viewModel.onSaveClicked(modelEditText.text.toString(), numberEditText.text.toString())
        }


    }

    inner class Adapter: PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> {
                    "Vehicle Model"
                }
                1 -> {
                    "Vehicle Number"
                }
                else -> {
                    null
                }
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return when(position) {
                0 -> {
                    modelContainer = LayoutInflater.from(this@EditCarActivity).inflate(R.layout.edit_vehicle_model, container, false)
                    container.addView(modelContainer)
                    modelEditText.setText(carInfo.model)
                    modelEditText.selectAll()
                    modelContainer!!
                }
                1 -> {
                    numberContainer = LayoutInflater.from(this@EditCarActivity).inflate(R.layout.edit_vehicle_number, container, false)
                    container.addView(numberContainer)
                    numberEditText.setText(carInfo.number)
                    numberEditText.selectAll()
                    scanNumberBtn.visibility = View.GONE
                    numberContainer!!
                }
                else -> {
                    throw IllegalStateException("Oooops")
                }
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    data class CarInfo(val model: String?, val number: String?): Serializable
}
