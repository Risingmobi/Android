package com.alex.tur.client.ui.splash

import android.os.Bundle
import com.alex.tur.base.BaseActivity
import com.alex.tur.client.ui.login.AuthActivity
import com.alex.tur.client.ui.main.MainActivity
import com.alex.tur.data.auth.MyAccountManager
import timber.log.Timber
import javax.inject.Inject

class SplashActivity: BaseActivity() {

    @Inject
    lateinit var myAccountManager: MyAccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.also { handleExtras(it) }

        if (myAccountManager.hasAccount()) {
            MainActivity.start(this)
            finish()
        } else {
            AuthActivity.start(this)
            finish()
        }
    }

    private fun handleExtras(extras: Bundle) {
        for(key in extras.keySet()) {
            Timber.tag("myfcm").i("key: %s, value: %s", key, extras[key])
        }
    }
}