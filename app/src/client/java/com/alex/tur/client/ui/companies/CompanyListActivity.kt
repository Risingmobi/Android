package com.alex.tur.client.ui.companies

import android.app.Activity
import android.arch.lifecycle.Observer
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
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.Result
import com.alex.tur.model.Service
import com.alex.tur.recycleradapter.SimpleCompositeAdapter
import kotlinx.android.synthetic.client.activity_company_list.*
import javax.inject.Inject

class CompanyListActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CompaniesViewModel>

    lateinit var viewModel: CompaniesViewModel

    @Inject
    lateinit var service: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_list)
        setSupportActionBar(toolbar)

        toolbar.title = service.naming

        val adapter = SimpleCompositeAdapter.Builder()
                .add(CompanyDelegateAdapter().apply {
                    setOnItemClickListener {
                        service.company = it
                        service.id = it.service_id
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra(EXTRA_SERVICE, service)
                        })
                        finish()
                    }
                })
                .build()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.requestCompanies(service.naming, true)
        }


        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CompaniesViewModel::class.java)
        lifecycle.addObserver(viewModel)

        viewModel.companies.observe(this, Observer {
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

        viewModel.requestCompanies(service.naming, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_company_list, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onQueryTextChange(service.naming, newText)
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

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            viewModel.finishing()
        }
    }

    companion object {

        const val EXTRA_SERVICE = "EXTRA_SERVICE"

        fun startForResult(activity: Activity, requestCode: Int, service: Service) {
            activity.startActivityForResult(Intent(activity, CompanyListActivity::class.java).apply {
                putExtra(EXTRA_SERVICE, service)
            }, requestCode)
        }

        fun startForResult(fragment: Fragment, requestCode: Int, service: Service) {
            fragment.startActivityForResult(Intent(fragment.context, CompanyListActivity::class.java).apply {
                putExtra(EXTRA_SERVICE, service)
            }, requestCode)
        }
    }
}
