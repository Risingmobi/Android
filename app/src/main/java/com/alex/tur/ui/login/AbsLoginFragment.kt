package com.alex.tur.ui.login

import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*

abstract class AbsLoginFragment : BaseFragment() {

    lateinit var callback: Callback

    companion object {
        const val TAG = "AbsLoginFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orSignUpBtn.setOnClickListener {
            callback.addSignUpFragment()
        }
    }

    interface Callback {
        fun addSignUpFragment()
        fun onForgotPasswordClicked()
    }
}