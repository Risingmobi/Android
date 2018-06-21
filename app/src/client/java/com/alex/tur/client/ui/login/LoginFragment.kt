package com.alex.tur.client.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.di.module.ui.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : BaseFragment() {

    lateinit var callback: Callback

    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AuthViewModel>

    companion object {
        const val TAG = "LoginFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orSignUpContainer.visibility = View.VISIBLE
        orSignUpBtn.setOnClickListener {
            callback.addSignUpFragment()
        }
        forgotPasswordBtn.setOnClickListener {
            callback.onForgotPasswordClicked()
        }

        loginBtn.setOnClickListener {
            viewModel.tryToLogin(emailEditText.text.toString(), passwordEditText.text.toString())
        }

        passwordEditText.setOnKeyListener{ v, keyCode, event ->
            event?.let {
                if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    viewModel.tryToLogin(emailEditText.text.toString(), passwordEditText.text.toString())
                    return@setOnKeyListener true
                }

            }
            false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AuthViewModel::class.java)

        viewModel.loginEmailValidation.observe(viewLifecycleOwner, Observer {
            emailTextInputLayout.error = it
        })

        viewModel.loginPasswordValidation.observe(viewLifecycleOwner, Observer {
            passwordTextInputLayout.error = it
        })
    }

    interface Callback {
        fun addSignUpFragment()
        fun onForgotPasswordClicked()
    }
}
