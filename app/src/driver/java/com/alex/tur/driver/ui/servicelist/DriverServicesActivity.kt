package com.alex.tur.driver.ui.servicelist

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.Result
import com.alex.tur.model.Service
import com.alex.tur.recycleradapter.SimpleCompositeAdapter
import kotlinx.android.synthetic.driver.activity_driver_services.*
import timber.log.Timber
import javax.inject.Inject

class DriverServicesActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<DriverServicesViewModel>

    lateinit var viewModel: DriverServicesViewModel

    lateinit var adapter: SimpleCompositeAdapter

    companion object {
        fun start(activity: Activity?) {
            activity?.startActivity(Intent(activity, DriverServicesActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_services)
        setSupportActionBar(toolbar)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.requestServices(true)
        }

        saveBtn.setOnClickListener {
            viewModel.save()
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DriverServicesViewModel::class.java)
        lifecycle.addObserver(viewModel)

        viewModel.services.observe(this, Observer {
            Timber.d("services observe %s", it?.status)
            it.let {
                when(it?.status) {
                    Result.Status.SUCCESS -> {
                        swipeRefreshLayout.isRefreshing = false
                        attachData(it.data)
                    }
                    Result.Status.ERROR -> {
                        swipeRefreshLayout.isRefreshing = false
                        attachData(it.data)
                        showError(it.message)
                    }
                    Result.Status.LOADING -> {
                        swipeRefreshLayout.isRefreshing = true
                        attachData(it.data)
                    }
                    null -> {}
                }
            }
        })

        viewModel.saveServices.observe(this, Observer {
            Timber.d("saveServices observe %s", it?.status)
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    finish()
                }
                Result.Status.ERROR -> {
                    hideLoading()
                    showError(it.message)
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
                null -> {}
            }
        })

        viewModel.notifyDataChangedAction.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })

        adapter = SimpleCompositeAdapter.Builder()
                .add(ServiceDelegateAdapter())
                .build()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        viewModel.requestServices(savedInstanceState == null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_driver_services_selector, menu)
        return true
    }

    private fun attachData(list: MutableList<Service>?) {
        adapter.swapData(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            viewModel.finishing()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.reset -> {
                viewModel.reset()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}