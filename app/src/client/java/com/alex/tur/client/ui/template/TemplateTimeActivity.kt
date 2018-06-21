package com.alex.tur.client.ui.template

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.getViewModel
import com.alex.tur.helper.Result
import com.alex.tur.model.DayOfWeek
import com.alex.tur.model.Template
import kotlinx.android.synthetic.client.activity_template_time.*
import kotlinx.android.synthetic.main.profile_item.view.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TemplateTimeActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<TemplateViewModel>

    lateinit var viewModel: TemplateViewModel

    companion object {

        const val EXTRA_TEMPLATE = "EXTRA_TEMPLATE"

        fun startForResult(fragment: Fragment, template: Template, requestCode: Int) {
            fragment.startActivityForResult(Intent(fragment.context, TemplateTimeActivity::class.java).apply {
                putExtra(EXTRA_TEMPLATE, template)
            }, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_time)
        setSupportActionBar(toolbar)

        viewModel = getViewModel(TemplateViewModel::class, viewModelFactory)

        timePicker.setIs24HourView(true)

        repeatView.icon.visibility = View.GONE
        repeatView.paramTextView.text = "To Repeat"
        repeatView.setOnClickListener {
            AlertDialog.Builder(this)
                    .setItems(R.array.template_repeat_cases, { dialog, wich ->
                        repeatView.valueTextView.text = resources.getStringArray(R.array.template_repeat_cases)[wich]
                        when(wich) {
                            0 -> viewModel.setSelectedDayOfWeek(DayOfWeek.MONDAY)
                            1 -> viewModel.setSelectedDayOfWeek(DayOfWeek.TUESDAY)
                            2 -> viewModel.setSelectedDayOfWeek(DayOfWeek.WEDNESDAY)
                            3 -> viewModel.setSelectedDayOfWeek(DayOfWeek.THURSDAY)
                            4 -> viewModel.setSelectedDayOfWeek(DayOfWeek.FRIDAY)
                            5 -> viewModel.setSelectedDayOfWeek(DayOfWeek.SATURDAY)
                            6 -> viewModel.setSelectedDayOfWeek(DayOfWeek.SUNDAY)
                        }
                    })
                    .show()
        }

        doneBtn.setOnClickListener {
            viewModel.createTemplate(timePicker.currentHour, timePicker.currentMinute)
        }


        val calendar = Calendar.getInstance()
        val days = resources.getStringArray(R.array.template_repeat_cases)
        when(calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> {
                viewModel.setSelectedDayOfWeek(DayOfWeek.SUNDAY)
                repeatView.valueTextView.text = days[6]
            }
            2 -> {
                viewModel.setSelectedDayOfWeek(DayOfWeek.MONDAY)
                repeatView.valueTextView.text = days[0]
            }
            3 -> {
                viewModel.setSelectedDayOfWeek(DayOfWeek.TUESDAY)
                repeatView.valueTextView.text = days[1]
            }
            4 -> {
                viewModel.setSelectedDayOfWeek(DayOfWeek.WEDNESDAY)
                repeatView.valueTextView.text = days[2]
            }
            5 -> {
                viewModel.setSelectedDayOfWeek(DayOfWeek.THURSDAY)
                repeatView.valueTextView.text = days[3]
            }
            6 -> {
                viewModel.setSelectedDayOfWeek(DayOfWeek.FRIDAY)
                repeatView.valueTextView.text = days[4]
            }
            7 -> {
                viewModel.setSelectedDayOfWeek(DayOfWeek.SATURDAY)
                repeatView.valueTextView.text = days[5]
            }
        }

        viewModel.createTemplate.observe(this, Observer {
            Timber.d("creating $it")
            when(it?.status) {
                Result.Status.LOADING -> {
                    showLoading()
                }
                Result.Status.SUCCESS -> {
                    hideLoading()
                }
                Result.Status.ERROR -> {
                    hideLoading()
                    showError(it.message)
                }
            }
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_template_time, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_reset -> {

            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
