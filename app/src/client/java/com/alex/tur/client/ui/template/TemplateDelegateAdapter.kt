package com.alex.tur.client.ui.template

import com.alex.tur.R
import com.alex.tur.model.api.ResponseTemplate
import com.alex.tur.recycleradapter.BaseDelegateAdapter
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import kotlinx.android.synthetic.client.item_template.*
import kotlinx.android.synthetic.main.profile_item.view.*

class TemplateDelegateAdapter: BaseDelegateAdapter() {

    private var removeItemClickListener: ((ResponseTemplate) -> Unit)? = null

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is ResponseTemplate
    }

    override fun getLayoutId(): Int {
        return R.layout.item_template
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        val template = dataHolder.data as ResponseTemplate
//        holder.addressView.paramTextView.text = "address"
//        holder.serviceView.paramTextView.text = template.serviceDescription?.id.toString()
//        holder.timeView.paramTextView.text = template.time
    }

    fun setOnRemoveItemClickListener(listener: (ResponseTemplate) -> Unit) {
        removeItemClickListener = listener
    }
}