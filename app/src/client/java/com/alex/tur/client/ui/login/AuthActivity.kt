package com.alex.tur.client.ui.login

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.client.ui.main.MainActivity
import com.alex.tur.client.ui.tutorial.TutorialActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.Result
import javax.inject.Inject

class AuthActivity : BaseActivity(), SignUpFragment.Callback, LoginFragment.Callback {

    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AuthViewModel>

    companion object {
        fun start(activity: Activity?) {
            activity?.startActivity(Intent(activity, AuthActivity::class.java))
        }

        private const val FRAGMENT_STACK = "AUTH_STACK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            addLoginFragment()
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel::class.java)

        viewModel.signUp.observe(this, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    onLoggedIn(it.data)
                }
                Result.Status.ERROR -> {
                    hideLoading()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
            }
        })

        viewModel.login.observe(this, Observer {
            when(it?.status) {
                Result.Status.SUCCESS -> {
                    onLoggedIn(it.data)
                }
                Result.Status.ERROR -> {
                    hideLoading()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                Result.Status.LOADING -> {
                    showLoading()
                }
            }
        })
    }

    private fun onLoggedIn(isFirstLaunch: Boolean?) {
        when(isFirstLaunch) {
            true -> {
                TutorialActivity.start(this)
                finish()
            }
            false -> {
                MainActivity.start(this)
                finish()
            }
        }
    }

    override fun popSignUpFragment() {
        supportFragmentManager.popBackStack()
    }

    private fun addLoginFragment() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(0, R.animator.fragment_fade_out, R.animator.fragment_fade_in, 0)
                .add(R.id.container, LoginFragment(), LoginFragment.TAG)
                .commit()
    }

    override fun onForgotPasswordClicked() {
        //TODO
        Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show()
    }

    override fun addSignUpFragment() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_out, R.animator.fragment_fade_in, R.animator.fragment_fade_out)
                .replace(R.id.container, SignUpFragment(), SignUpFragment.TAG)
                .addToBackStack(FRAGMENT_STACK)
                .commit()
    }
}