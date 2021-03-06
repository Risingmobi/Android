package com.alex.tur.recycleradapter

import android.support.annotation.LayoutRes
import android.view.ViewGroup

interface IDelegateAdapter {

    fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    fun bindViewHolder(holder: BaseViewHolder, dataHolder: DataHolder, position: Int)

    fun onRecycled(holder: BaseViewHolder)

    fun isForViewType(items: List<DataHolder>, position: Int): Boolean

    @LayoutRes
    fun getLayoutId(): Int
}