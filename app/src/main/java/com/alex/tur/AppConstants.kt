package com.alex.tur

import android.util.Base64
import timber.log.Timber

object AppConstants {

    const val BASE_URL = ""
    const val PASSWORD_LENGTH = 3
    const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\\\S+\$).{4,}\$"

    private const val CLIENT_OAUTH_CLIENT_ID = "bhbbbbhb"
    private const val CLIENT_OAUTH_CLIENT_SECRET = "ijijijijijij"
    private const val DRIVER_OAUTH_CLIENT_ID = "oookokokokoko"
    private const val DRIVER_OAUTH_CLIENT_SECRET = "ffftfftftftft"

    const val OAUTH_TOKEN_TYPE = "Bearer"
    const val OAUTH_GRANT_TYPE = "password"

    const val INVALID_ID = -1

    private fun getClientId(): String {
        return when(BuildConfig.FLAVOR) {
            "client" -> CLIENT_OAUTH_CLIENT_ID
            "driver" -> DRIVER_OAUTH_CLIENT_ID
            else -> throw IllegalArgumentException("Flavour not found")
        }
    }

    private fun getClientSecret(): String {
        return when(BuildConfig.FLAVOR) {
            "client" -> CLIENT_OAUTH_CLIENT_SECRET
            "driver" -> DRIVER_OAUTH_CLIENT_SECRET
            else -> throw IllegalArgumentException("Flavour not found")
        }
    }

    fun getAuthHeaderForLogin(): String {
        val source = getClientId() + ":" + getClientSecret()
        Timber.d("getAuthHeaderForLogin %s", source)
        val sourceBytes = source.toByteArray()
        val encodedBytes = Base64.encode(sourceBytes, Base64.NO_WRAP)
        return "Basic " + String(encodedBytes, Charsets.UTF_8)
    }
}