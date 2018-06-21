package com.alex.tur.client.ui.history

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.client.ui.orders.OrderDelegateAdapter
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.getViewModel
import com.alex.tur.recycleradapter.SimpleCompositeAdapter
import kotlinx.android.synthetic.client.activity_history.*
import javax.inject.Inject

class HistoryActivity : BaseActivity() {

    companion object {
        fun start(fragment: Fragment) {
            fragment.startActivity(Intent(fragment.activity, HistoryActivity::class.java))
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HistoryViewModel>

    lateinit var viewModel: HistoryViewModel

    lateinit var adapter: SimpleCompositeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        viewModel = getViewModel(HistoryViewModel::class, viewModelFactory)

        adapter = SimpleCompositeAdapter.Builder()
                .add(OrderDelegateAdapter())
                .build()

        val recyclerView = RecyclerView(this)
//        recyclerView.setPadding(0, DimensUtils.dpToPx(24f), 0, DimensUtils.dpToPx(60f))
        recyclerView.clipToPadding = false
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataContainer.attachContentView(recyclerView)
        dataContainer.emptyView.button.visibility = View.VISIBLE
        dataContainer.emptyView.button.text = getString(R.string.reload)
        dataContainer.setOnRefreshListener {
            viewModel.onRefresh(true)
        }

        viewModel.history.observe(this, Observer {
            dataContainer.setResult(it, {
                it == null || it.size == 0
            })
            adapter.swapData(it?.data)
        })
    }
}