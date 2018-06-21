package com.alex.tur.client.ui.comment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.getViewModel
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.client.fragment_comment_text.*
import javax.inject.Inject

class CommentTextFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CommentViewModel>

    lateinit var viewModel: CommentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comment_text, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel(CommentViewModel::class, viewModelFactory)

        viewModel.comment.observe(viewLifecycleOwner, Observer {
            editText.setText(it?.brieflyDescription)
        })

        RxTextView.textChanges(editText)
                .skipInitialValue()
                .subscribe({
                    viewModel.onTextCommentChanged(it.toString())
                })
    }
}
