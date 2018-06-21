package com.alex.tur.recycleradapter.section

import android.support.annotation.LayoutRes
import android.view.ViewGroup
import com.alex.tur.recycleradapter.BaseViewHolder
import com.alex.tur.recycleradapter.DataHolder

interface IHeaderFooterDelegateAdapter {

    fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    fun bindViewHolder(holder: BaseViewHolder, item: DataHolder)

    fun onRecycled(holder: BaseViewHolder)

    @LayoutRes
    fun getLayoutId(): Int
}