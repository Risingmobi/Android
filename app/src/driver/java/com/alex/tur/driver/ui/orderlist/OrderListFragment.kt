package com.alex.tur.driver.ui.orderlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.MyBottomSheetBehavior
import com.alex.tur.helper.Result
import com.alex.tur.model.Order
import com.alex.tur.recycleradapter.section.Section
import com.alex.tur.recycleradapter.section.SectionedCompositeAdapter
import com.alex.tur.utils.DimensUtils
import kotlinx.android.synthetic.driver.fragment_order_list.*
import timber.log.Timber
import javax.inject.Inject

class OrderListFragment : BaseFragment(), ReasonDialogFragment.OnReasonListener {

    private lateinit var bottomSheetBehavior: MyBottomSheetBehavior<View>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<OrderListViewModel>

    lateinit var viewModel: OrderListViewModel

    private lateinit var callback: Callback

    private lateinit var adapter: SectionedCompositeAdapter

    private val ordersHandler = Handler()

    private val activeOrderObserver = Observer<Result<Order>> {
        Timber.tag("ghjhgjhnh").i("list activeOrderObserver %s, %s, %s", it?.status, it?.type, it?.data != null)
        val activeOrder = it?.data

        viewModel.currentLocation.value?.also { location ->
            activeOrder?.also {order ->
                val res = FloatArray(3)
                Location.distanceBetween(order.lat!!, order.lng!!, location.latitude, location.longitude, res)
                Timber.tag("sfdsfv").d("activeOrderObserver %s", res[0])
                order.isReadyForCompletion = res[0] < 50
            }
        }

        adapter.setHeaderDataInSection(0, it?.data)

        when(it?.status) {
            Result.Status.SUCCESS -> {

            }
            Result.Status.ERROR -> {
                showError(it.message)
            }
            Result.Status.LOADING -> {}
            null -> {}
        }

        if (it?.data != null || viewModel.pendingOrderList.value != null) {
            show()
        } else {
            hide()
        }
    }

    private val currentLocationObserver = Observer<Location> {
        val activeOrder = viewModel.activeOrder.value?.data

        it?.also { location ->
            activeOrder?.also {order ->
                val res = FloatArray(3)
                Location.distanceBetween(order.lat!!, order.lng!!, location.latitude, location.longitude, res)
                Timber.tag("sfdsfv").d("currentLocationObserver %s", res[0])
                order.isReadyForCompletion = res[0] < 50
            }
        }
        adapter.setHeaderDataInSection(0, activeOrder)
    }

    private val pendingOrderListObserver = Observer<Result<MutableList<Order>>> {
        Timber.tag("ghjhgjhnh").w("list pendingOrderListObserver %s, %s, %s", it?.status, it?.type, it?.data?.size)
        adapter.swapDataInSection(0, it?.data)

        when(it?.status) {
            Result.Status.SUCCESS -> {

            }
            Result.Status.ERROR -> {
                showError(it.message)
            }
            Result.Status.LOADING -> {}
            null -> {}
        }

        if ((it?.data != null && it.data!!.isNotEmpty()) || viewModel.activeOrder.value != null) {
            show()
        } else {
            hide()
        }
    }

    private val closeActiveOrderObserver = Observer<Result<Unit>> {
        when(it?.status) {
            Result.Status.SUCCESS -> {
                viewModel.requestActiveOrder(true)
                viewModel.requestPendingOrderList(true)
            }
            Result.Status.ERROR -> {
                showError("Error while completing order")
                adapter.changeHeader(0, {
                    it?.also { headerData ->
                        val params = hashMapOf<Int, Any>()
                        params[ActiveOrderDelegateAdapter.IS_READY_FOR_CLOSE] = false
                        headerData.change(params)
                    }
                })
            }
            Result.Status.LOADING -> {
                adapter.changeHeader(0, {
                    it?.also { headerData ->
                        val params = hashMapOf<Int, Any>()
                        params[ActiveOrderDelegateAdapter.IS_READY_FOR_CLOSE] = true
                        headerData.change(params)
                    }
                })
            }
            null -> {}
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetBehavior = MyBottomSheetBehavior.from(bottomSheetLayout, "ORDER_LIST")
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = DimensUtils.dpToPx(220f)
        bottomSheetBehavior.setBottomSheetCallback(object : MyBottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                callback.onOrderListBottomSheetSlide(bottomSheet, slideOffset)
                Timber.d("onSlide %s", slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    MyBottomSheetBehavior.STATE_EXPANDED -> {
                        Timber.d("STATE_EXPANDED")
                    }
                    MyBottomSheetBehavior.STATE_COLLAPSED -> {
                        Timber.d("STATE_COLLAPSED")
                    }
                }
                callback.onOrderListBottomSheetStateChanged(bottomSheet, newState)
            }
        })

        hide()

        adapter = SectionedCompositeAdapter.Builder()
                .add(Section.Builder()
                        .setHeader(ActiveOrderDelegateAdapter().apply {
                            setClickListener(object : ActiveOrderDelegateAdapter.ClickListener {
                                override fun onDoneClicked(order: Order) {
                                    viewModel.completeOrder(order)
                                }
                                override fun onDeleteClicked(order: Order) {
                                    viewModel.onDeleteOrderClicked(order)
                                }
                            })
                        })
                        .add(PendingOrderDelegateAdapter().apply {
                            setClickListener(object : PendingOrderDelegateAdapter.ClickListener {
                                override fun onDeleteClicked(order: Order) {
                                    viewModel.onDeleteOrderClicked(order)
                                }
                            })
                        })
                        .build())
                .build()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

//        recyclerView.addOnLayoutChangeListener({ v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//            if (recyclerView.childCount > 0) {
//                bottomSheetBehavior.peekHeight = recyclerView.getChildAt(0).height
//                Timber.d("OnLayoutChangeListener %s", bottomSheetBehavior.peekHeight)
//
//            }
//        })

        /*recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                if (recyclerView.childCount > 0) {
                    recyclerView.removeOnLayoutChangeListener(this)
                    bottomSheetBehavior.peekHeight = recyclerView.getChildAt(0).height
                    Timber.d("OnLayoutChangeListener %s", bottomSheetBehavior.peekHeight)
                }
                Timber.tag("SectionedCompositeAdapter").d("onLayoutChange")
            }
        })*/

        bottomSheetLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                if (bottomSheetLayout.height != 0) {
                    bottomSheetLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback.onOrderListBottomSheetSlide(bottomSheetLayout, bottomSheetBehavior.slideOffset)
                    callback.onOrderListBottomSheetStateChanged(bottomSheetLayout, bottomSheetBehavior.state)
                }
            }
        })

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(OrderListViewModel::class.java)
        lifecycle.addObserver(viewModel)
        viewModel.pendingOrderList.observe(viewLifecycleOwner, pendingOrderListObserver)
        viewModel.activeOrder.observe(viewLifecycleOwner, activeOrderObserver)
        viewModel.currentLocation.observe(viewLifecycleOwner, currentLocationObserver)
        viewModel.closeActiveOrderResult.observe(viewLifecycleOwner, closeActiveOrderObserver)
        viewModel.deleteOrderResult.observe(viewLifecycleOwner, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    Toast.makeText(context, "Order removed", Toast.LENGTH_SHORT).show()
                    viewModel.requestActiveOrder(true)
                    viewModel.requestPendingOrderList(true)
                }
                Result.Status.ERROR -> {
                    showError(it.message)
                }
                Result.Status.LOADING -> {

                }
            }
        })
        viewModel.showReasonDialog.observe(viewLifecycleOwner, Observer {
            it?.id?.also {
                ReasonDialogFragment.newInstance(it).show(childFragmentManager, ReasonDialogFragment.TAG)
            }
        })

        requestOrderList()
    }

    private fun requestOrderList() {
        viewModel.requestPendingOrderList(true)
        viewModel.requestActiveOrder(true)
        ordersHandler.postDelayed({
            requestOrderList()
        }, 30000)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        ordersHandler.removeCallbacksAndMessages(null)
        viewModel.pendingOrderList.removeObserver(pendingOrderListObserver)
        viewModel.activeOrder.removeObserver(activeOrderObserver)
        viewModel.currentLocation.removeObserver(currentLocationObserver)
        viewModel.closeActiveOrderResult.removeObserver(closeActiveOrderObserver)
    }

    private fun collapse() {
        bottomSheetBehavior.state = MyBottomSheetBehavior.STATE_COLLAPSED
        recyclerView.smoothScrollToPosition(0)
    }

    private fun hide() {
        bottomSheetLayout.visibility = View.GONE
        collapse()
    }

    fun show() {
        bottomSheetLayout.visibility = View.VISIBLE
    }

    override fun onReasonInteraction(orderId: Int, reason: String) {
        viewModel.deleteOrder(orderId, reason)
    }

    fun onBackPressed(): Boolean {
        return if (isVisible && bottomSheetBehavior.state != MyBottomSheetBehavior.STATE_COLLAPSED) {
            collapse()
            true
        } else {
            false
        }
    }

    interface Callback {
        fun onOrderListBottomSheetSlide(bottomSheet: View, slideOffset: Float)
        fun onOrderListBottomSheetStateChanged(bottomSheet: View, newState: Int)
    }
}
