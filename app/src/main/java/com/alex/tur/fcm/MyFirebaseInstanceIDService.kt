package com.alex.tur.fcm

import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class MyFirebaseInstanceIDService: FirebaseInstanceIdService() {

    private val TAG = "myfcm"

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Timber.tag(TAG).d("onTokenRefresh: %s", refreshedToken)

        refreshedToken?.also {
            sendRegistrationToServer(it)
        }
    }

    private fun sendRegistrationToServer(token: String) {

    }
}