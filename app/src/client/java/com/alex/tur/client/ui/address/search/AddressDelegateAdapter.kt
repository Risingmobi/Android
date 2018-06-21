package com.alex.tur.client.ui.address.search

import android.support.annotation.CallSuper
import android.view.View
import com.alex.tur.R
import com.alex.tur.recycleradapter.BaseDelegateAdapter
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder
import com.alex.tur.recycleradapter.KViewHolder
import kotlinx.android.synthetic.main.address_search_list_item.*

abstract class AddressDelegateAdapter : BaseDelegateAdapter() {

    private var itemClickListener: ((AddressItem) -> Unit)? = null

    override fun getLayoutId(): Int {
        return R.layout.address_search_list_item
    }

    fun setOnItemClickListener(listener: (AddressItem) -> Unit) {
        itemClickListener = listener
    }

    @CallSuper
    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        holder.containerView.setOnClickListener {
            itemClickListener?.invoke(dataHolder.data as AddressItem)
        }
    }
}

class FoundAddressDelegateAdapter: AddressDelegateAdapter() {

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is FoundAddressItem
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        super.bindViewHolder(holder, dataHolder, position)
        val addressItem = dataHolder.data as FoundAddressItem
        holder.addressTextView.text = addressItem.description

    }
}

class CurrentAddressDelegateAdapter: AddressDelegateAdapter() {

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is CurrentAddressItem
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        super.bindViewHolder(holder, dataHolder, position)
        val addressItem = dataHolder.data as CurrentAddressItem
        holder.addressTextView.text = addressItem.description
        holder.icon.setImageResource(R.drawable.ic_my_location_black_24dp)
        holder.icon.visibility = View.VISIBLE
    }
}

class HomeAddressDelegateAdapter: AddressDelegateAdapter() {

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is HomeAddressItem
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        super.bindViewHolder(holder, dataHolder, position)
        val addressItem = dataHolder.data as HomeAddressItem
        holder.addressTextView.text = addressItem.description
        holder.icon.setImageResource(R.drawable.ic_home_black_24dp)
        holder.icon.visibility = View.VISIBLE
    }
}

class OnMapAddressDelegateAdapter: AddressDelegateAdapter() {

    override fun isForViewType(items: List<DataHolder>, position: Int): Boolean {
        return items[position].data is OnMapAddressItem
    }

    override fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int) {
        super.bindViewHolder(holder, dataHolder, position)
        val addressItem = dataHolder.data as OnMapAddressItem
        holder.addressTextView.text = addressItem.description
        holder.icon.setImageResource(R.drawable.ic_person_pin_circle_black_24dp)
        holder.icon.visibility = View.VISIBLE
    }
}