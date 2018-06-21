package com.alex.tur.data.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpAuthenticator @Inject constructor(): Authenticator {

    override fun authenticate(route: Route, response: Response): Request? {
//        if (response.request().header("Authorization") != null) {
//            return null // Give up, we've already attempted to authenticate.
//        }

        if (responseCount(response) >= 2) {
            return null // If we've failed 2 times, give up.
        }

        System.out.println("Authenticating for response: $response")
        System.out.println("Challenges: " + response.challenges())
//        val credential = Credentials.basic("jesse", "password1")
//        return response.request().newBuilder()
//                .header("Authorization", credential)
//                .build()

        return null
    }

    private fun responseCount(response: Response): Int {
        var resp: Response? = response
        var result = 0
        do {
            result++
            resp = resp?.priorResponse()
        }
        while (resp != null)

        return result
    }
}