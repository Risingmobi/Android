package com.alex.tur.client.ui.companies

import com.alex.tur.R
import com.alex.tur.helper.GlideHelper
import com.alex.tur.model.Company
import com.alex.tur.recycleradapter.BaseDelegateAdapter
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import kotlinx.android.synthetic.client.item_company_list_activity.*

class CompanyDelegateAdapter : BaseDelegateAdapter() {

    private var itemClickListener: ((Company) -> Unit)? = null

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is Company
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        val company = dataHolder.data as Company
        holder.nameTextView.text = company.naming
        holder.priceTextView.text = "$${company.cost}"
        if (company.picture == null) {
            GlideHelper.clear(holder.containerView.context, holder.icon)
        } else {
            GlideHelper.load(holder.containerView.context, holder.icon, company.picture)
        }

        holder.containerView.setOnClickListener {
            itemClickListener?.invoke(company)
        }
    }

    fun setOnItemClickListener(listener: (Company) -> Unit) {
        itemClickListener = listener
    }



    override fun getLayoutId(): Int {
        return R.layout.item_company_list_activity
    }
}