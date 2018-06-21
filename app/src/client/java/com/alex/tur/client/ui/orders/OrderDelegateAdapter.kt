package com.alex.tur.client.ui.orders

import com.alex.tur.R
import com.alex.tur.client.ui.address.search.AddressItem
import com.alex.tur.helper.GlideHelper
import com.alex.tur.model.Order
import com.alex.tur.model.OrderDescription
import com.alex.tur.recycleradapter.BaseDelegateAdapter
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import com.alex.tur.recycleradapter.KViewHolder
import kotlinx.android.synthetic.client.item_fragment_orders.*

class OrderDelegateAdapter : BaseDelegateAdapter() {

    private var avatarClickListener: ((Order) -> Unit)? = null
    private var attachmentClickListener: ((OrderDescription) -> Unit)? = null

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is Order
    }

    override fun getLayoutId(): Int {
        return R.layout.item_fragment_orders
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        val order = dataHolder.data as Order

        GlideHelper.loadAvatar(holder.containerView.context, holder.avatarImageView, order.willBeEvaluatedBy?.avatar)
        holder.nameTextView.text = order.willBeEvaluatedBy?.name
//        holder.nameTextView.text = order.id.toString()
        holder.commentTextView.text = order.orderDescription?.brieflyDescription
        holder.serviceTextView.text = order.service?.naming
        holder.priceTextView.text = "$${order.service?.cost}"

        holder.avatarImageView.setOnClickListener {
            avatarClickListener?.invoke(order)
        }
        holder.attachmentIcon.setOnClickListener {
            order.orderDescription?.also {
                attachmentClickListener?.invoke(it)
            }
        }
    }

    fun setOnAvatarClickListener(listener: (Order) -> Unit) {
        avatarClickListener = listener
    }

    fun setOnAttachmentClickListener(listener: (OrderDescription) -> Unit) {
        attachmentClickListener = listener
    }
}