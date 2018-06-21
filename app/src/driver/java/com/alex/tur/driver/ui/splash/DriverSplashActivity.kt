package com.alex.tur.driver.ui.splash

import android.os.Bundle
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.driver.ui.login.AuthActivity
import com.alex.tur.driver.ui.main.MainActivity
import com.alex.tur.base.BaseActivity
import javax.inject.Inject

class DriverSplashActivity: BaseActivity() {

    @Inject
    lateinit var myAccountManager: MyAccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (myAccountManager.hasAccount()) {
            MainActivity.start(this)
            finish()
        } else {
            AuthActivity.start(this)
            finish()
        }
    }
}