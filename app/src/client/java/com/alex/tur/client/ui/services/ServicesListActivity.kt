package com.alex.tur.client.ui.services

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.client.ui.companies.CompanyListActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.Result
import com.alex.tur.model.Company
import com.alex.tur.model.Service
import com.alex.tur.recycleradapter.SimpleCompositeAdapter
import kotlinx.android.synthetic.client.activity_services_list.*
import timber.log.Timber
import javax.inject.Inject

class ServicesListActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<ServicesViewModel>

    lateinit var viewModel: ServicesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_list)
        setSupportActionBar(toolbar)

        val adapter = SimpleCompositeAdapter.Builder()
                .add(ServiceDelegateAdapter().apply {
                    setOnItemClickListener {
                        CompanyListActivity.startForResult(this@ServicesListActivity, RC_COMPANIES, it)
                    }
                })
                .build()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.requestServices(true)
        }


        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ServicesViewModel::class.java)
        lifecycle.addObserver(viewModel)

        viewModel.services.observe(this, Observer {
            Timber.tag("ServicesViewModel").d("Activity observe %s", it?.status)
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.swapData(it.data)
                }
                Result.Status.ERROR -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.swapData(it.data)
                }
                Result.Status.LOADING -> {
                    swipeRefreshLayout.isRefreshing = true
                    adapter.swapData(it.data)
                }
                null -> {}
            }
        })

        viewModel.requestServices(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_services_list, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onQueryTextChange(newText)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_search -> {

            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RC_COMPANIES -> {
                if(resultCode == Activity.RESULT_OK) {
                    val service = data?.getSerializableExtra(CompanyListActivity.EXTRA_SERVICE) as Service
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(EXTRA_SERVICE, service)
                    })
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            viewModel.finishing()
        }
    }

    companion object {

        const val EXTRA_SERVICE = "EXTRA_SERVICE"
        const val RC_COMPANIES = 1

        fun startForResult(fragment: Fragment, requestCode: Int) {
            fragment.startActivityForResult(Intent(fragment.context!!, ServicesListActivity::class.java), requestCode)
        }
    }
}
