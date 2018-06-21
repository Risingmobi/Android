package com.alex.tur.driver.ui.orderlist

import android.support.v4.content.ContextCompat
import android.view.View
import com.alex.tur.R
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.GlideHelper
import com.alex.tur.model.Order
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import com.alex.tur.recycleradapter.section.BaseHeaderFooterDelegateAdapter
import com.daimajia.swipe.SwipeLayout
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.driver.item_order.*
import timber.log.Timber

class ActiveOrderDelegateAdapter: BaseHeaderFooterDelegateAdapter() {

    private var itemClickListener: ClickListener? = null

    fun setClickListener(itemClickListener: ClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getLayoutId(): Int {
        return R.layout.item_order
    }

    override fun onCreateViewHolder(view: View): BaseViewHolder {
        Timber.d("onCreateViewHolder")
        val viewHolder = ViewHolder(view)
        val swipeLayout = viewHolder.containerView as SwipeLayout
//        swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
//        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.containerView.content)
        return viewHolder
    }

    inner class ViewHolder(containerView: View) : BaseViewHolder(containerView), DataHolder.EventListener {

        private var addressDisposable: Disposable? = null
        private val addressFetcher = AddressFetcher(containerView.context)

        override fun onBind(dataHolder: DataHolder) {
            Timber.d("onBind %s", dataHolder.data)
            super.onBind(dataHolder)
            val order = dataHolder.data as Order
            numberTextView.text = (adapterPosition + 1).toString()
            GlideHelper.loadAvatar(containerView.context, avatarImageView, order.assignTo?.avatar)
            nameTextView.text = order.assignTo?.name
            serviceTextView.text = order.service?.naming

            timeTextView.text = order.estimationDurationAndDistance?.duration?.text
            distanceTextView.text = order.estimationDurationAndDistance?.distance?.text

            addressTextView.text = order.address
//            addressDisposable = Single.fromCallable { addressFetcher.fetchAddressString(order.lat, order.lng) }
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        addressTextView.text = it
//                    }, {
//                        Timber.e(it)
//                    })

            val tintColor = ContextCompat.getColor(containerView.context, R.color.colorAccent)

            numberTextView.setTextColor(tintColor)
            avatarImageView.borderColor = tintColor
            nameTextView.setTextColor(tintColor)
            timeIcon.setColorFilter(tintColor)
            timeTextView.setTextColor(tintColor)
            distanceIcon.setColorFilter(tintColor)
            distanceTextView.setTextColor(tintColor)

            if (order.isReadyForCompletion) {
                doneBtn.visibility = View.VISIBLE
            } else {
                doneBtn.visibility = View.GONE
            }
//            doneBtn.visibility = View.VISIBLE

            doneBtn.setOnClickListener {
                itemClickListener?.onDoneClicked(order)
            }

            deleteBtn.setOnClickListener {
                itemClickListener?.onDeleteClicked(order)
            }
        }

        override fun onChange(params: Map<Int, Any>) {
            for (key in params.keys) {
                when(key) {
                    IS_READY_FOR_CLOSE -> {

                    }
                }
            }
        }

        override fun onRecycled() {
            super.onRecycled()
            addressDisposable?.dispose()

        }
    }

    interface ClickListener {
        fun onDoneClicked(order: Order)
        fun onDeleteClicked(order: Order)
    }

    companion object {
        const val IS_READY_FOR_CLOSE = 0
    }
}