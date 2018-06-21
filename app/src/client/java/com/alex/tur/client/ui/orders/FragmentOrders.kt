package com.alex.tur.client.ui.orders

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.client.ui.comment.CommentActivity
import com.alex.tur.client.ui.history.HistoryActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.recycleradapter.SimpleCompositeAdapter
import com.alex.tur.utils.DimensUtils
import kotlinx.android.synthetic.main.fragment_orders.*
import javax.inject.Inject

class FragmentOrders : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<OrdersViewModel>

    lateinit var viewModel: OrdersViewModel

    lateinit var adapter: SimpleCompositeAdapter

    lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SimpleCompositeAdapter.Builder()
                .add(OrderDelegateAdapter().apply {
                    setOnAvatarClickListener{ viewModel.onItemClicked(it) }
                    setOnAttachmentClickListener{ viewModel.onAttachmentClicked(it) }
                })
                .build()

        val recyclerView = RecyclerView(context)
        recyclerView.setPadding(0, DimensUtils.dpToPx(24f), 0, DimensUtils.dpToPx(60f))
        recyclerView.clipToPadding = false
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        historyBtn.setOnClickListener {
            viewModel.onHistoryClicked()
        }

        dataContainer.attachContentView(recyclerView)
        dataContainer.setEmptyImageResource(R.drawable.empty_purchases)
        dataContainer.setEmptyMessageResource(R.string.empty_purchases)
        dataContainer.emptyView.button.visibility = View.VISIBLE
        dataContainer.emptyView.button.text = getString(R.string.request_service)
        dataContainer.setOnRefreshListener {
            viewModel.requestOrders(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(OrdersViewModel::class.java)
        lifecycle.addObserver(viewModel)

        viewModel.orderListResult.observe(viewLifecycleOwner, Observer {
            dataContainer.setResult(it, {
                it == null || it.size == 0
            })
            adapter.swapData(it?.data)
        })
        viewModel.goToMapAction.observe(viewLifecycleOwner, Observer {
            callback.goToMapFromOrderList()
        })
        viewModel.openCommentAction.observe(viewLifecycleOwner, Observer {
            it?.also { CommentActivity.start(this, it) }
        })
        viewModel.openHistoryAction.observe(viewLifecycleOwner, Observer {
            HistoryActivity.start(this)
        })

        viewModel.requestOrders()
    }

    interface Callback {
        fun goToMapFromOrderList()
    }

    companion object {
        const val TAG = "FragmentOrders"
    }
}