package com.alex.tur.ui.login

import android.os.Bundle
import android.widget.Toast
import com.alex.tur.R
import com.alex.tur.base.BaseActivity

abstract class AbsAuthActivity : BaseActivity(), AbsLoginFragment.Callback {

    companion object {
        const val EXTRA_TOKEN_TYPE = "EXTRA_TOKEN_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            addLoginFragment()
        }
    }

    override fun onForgotPasswordClicked() {
        Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show()
    }

    abstract fun addLoginFragment()
    abstract fun onLoggedIn()
}