package com.alex.tur.driver.ui.orderlist

import android.view.View
import com.alex.tur.R
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.GlideHelper
import com.alex.tur.model.Order
import com.alex.tur.recycleradapter.BaseDelegateAdapter
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import com.alex.tur.recycleradapter.KViewHolder
import com.alex.tur.recycleradapter.section.Section
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.driver.item_order.*
import timber.log.Timber

class PendingOrderDelegateAdapter: BaseDelegateAdapter() {

    private var itemClickListener: ClickListener? = null

    fun setClickListener(itemClickListener: ClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is Order
    }

    override fun getLayoutId(): Int {
        return R.layout.item_order
    }

    override fun onCreateViewHolder(view: View): BaseViewHolder {
        return ViewHolder(view)
    }

    inner class ViewHolder(containerView: View) : BaseViewHolder(containerView) {

        private var addressDisposable: Disposable? = null
        private val addressFetcher = AddressFetcher(containerView.context)

        override fun onBind(dataHolder: DataHolder) {
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

            doneBtn.visibility = View.GONE

            deleteBtn.setOnClickListener {
                itemClickListener?.onDeleteClicked(order)
            }
        }

        override fun onRecycled() {
            super.onRecycled()
            addressDisposable?.dispose()
        }
    }

    interface ClickListener {
        fun onDeleteClicked(order: Order)
    }
}