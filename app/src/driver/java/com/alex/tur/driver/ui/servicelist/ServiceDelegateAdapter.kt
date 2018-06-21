package com.alex.tur.driver.ui.servicelist

import com.alex.tur.R
import com.alex.tur.model.Service
import com.alex.tur.recycleradapter.BaseDelegateAdapter
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import com.alex.tur.recycleradapter.KViewHolder
import kotlinx.android.synthetic.driver.driver_service_item.*

class ServiceDelegateAdapter: BaseDelegateAdapter() {

    override fun isForViewType(items: List<DataHolder>, position: Int) = items[position].data is Service

    override fun getLayoutId() = R.layout.driver_service_item

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        val service = dataHolder.data as Service
        holder.nameTextView.text = service.naming

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = service.isChecked
        holder.checkBox.setOnCheckedChangeListener({ _, isChecked ->
            service.isChecked = isChecked
        })
    }
}