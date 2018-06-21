package com.alex.tur.client.ui.tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.helper.GlideHelper
import kotlinx.android.synthetic.client.fragment_tutorial.*

private const val ARG_DATA = "ARG_DATA"

class TutorialFragment : Fragment() {
    private lateinit var data: Tut

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getSerializable(ARG_DATA) as Tut
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GlideHelper.load(context, image, data.image)
        titleTextView.text = data.title
        descTextView.text = data.desc
    }

    companion object {

        @JvmStatic
        fun newInstance(data: Tut) =
                TutorialFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_DATA, data)
                    }
                }
    }
}
