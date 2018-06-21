package com.alex.tur.client.ui.services

import com.alex.tur.R
import com.alex.tur.helper.GlideHelper
import com.alex.tur.model.Service
import com.alex.tur.recycleradapter.BaseDelegateAdapter
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import kotlinx.android.synthetic.client.item_service_list_acivity.*

class ServiceDelegateAdapter : BaseDelegateAdapter() {

    private var itemClickListener: ((Service) -> Unit)? = null

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is Service
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        val service = dataHolder.data as Service
        holder.nameTextView.text = service.naming
        if (service.picture == null) {
            GlideHelper.clear(holder.containerView.context, holder.icon)
        } else {
            GlideHelper.load(holder.containerView.context, holder.icon, service.picture)
        }

        holder.containerView.setOnClickListener {
            itemClickListener?.invoke(service)
        }
    }

    fun setOnItemClickListener(listener: (Service) -> Unit) {
        itemClickListener = listener
    }



    override fun getLayoutId(): Int {
        return R.layout.item_service_list_acivity
    }

}
