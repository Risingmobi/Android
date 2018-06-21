package com.alex.tur.interceptor

import android.content.Context
import com.alex.tur.R
import com.alex.tur.data.net.NetworkChecker
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.error.HttpErrorBody
import com.alex.tur.error.MyException
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorInterceptor
@Inject
constructor(@QualifierAppContext private val context: Context,
            private val networkChecker: NetworkChecker) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val requestBuilder = chain.request().newBuilder()
        if (!networkChecker.isConnected) {
            throwException(context.getString(R.string.error_no_network), context.getString(R.string.error_no_network))
        }

        try {
            val response = chain.proceed(requestBuilder.build())
            checkForHttpErrors(response)
            return response
        } catch (e: SocketTimeoutException) {
            Timber.e(e, "my SocketTimeoutException")
            throwException(context.getString(R.string.error_timeout), context.getString(R.string.error_timeout))
        } catch (e: UnknownHostException) {
            Timber.e(e, "my UnknownHostException")
            throwException(context.getString(R.string.error_connection), context.getString(R.string.error_connection))
        } catch (e: SocketException) {
            Timber.e(e, "my SocketException")
            throwException(context.getString(R.string.error_connection), context.getString(R.string.error_connection))
        }
        return null
    }

    @Throws(Exception::class)
    private fun checkForHttpErrors(response: Response) {
        Timber.d("checkForHttpErrors %s, %s, %s", response.code(), response.request().url(), response.message())
        if (!response.isSuccessful) {
            var localizedMessageRes = R.string.some_error
            var body: HttpErrorBody? = null
            try {
                val bodyString = response.body()?.string()
                Timber.w(bodyString)
                val gson = Gson()
                body = gson.fromJson(bodyString, HttpErrorBody::class.java)
            } catch (exc: Exception) {
                //ignore
            }
            Timber.d("checkForHttpErrors body %s", body?.error)

            when (response.code()) {
                400 -> {
                    body?.error?.let {
                        if (it == "User email or password are incorrect") {
                            localizedMessageRes = R.string.error_email_or_password_incorrect
                        }
                    }
                }
                418 -> {
                    body?.error?.let {
                        if (it == "No active orders") {
                            localizedMessageRes = R.string.error_no_active_orders
                        }
                    }
                }
                405 -> {
                    body?.error?.let {
                        if (it == "You have not permissions") {
                            localizedMessageRes = R.string.error_no_permission
                        }
                    }
                }
                500 -> {
                    localizedMessageRes = R.string.internal_server_error
                    when(body?.error) {
                        "Applications with this credentials does not exists" -> {
                            localizedMessageRes = R.string.error_credentials_does_not_exist
                        }
                        "Can't calculate nearest driver" -> {
                            localizedMessageRes = R.string.error_can_not_calculate_nearest_driver
                        }
                        "Can't calculate duration and distance" -> {
                            localizedMessageRes = R.string.error_can_not_calculate_duration_and_distance
                        }
                    }
                }
            }

            throwException(body?.error ?: "Some error occurred", context.getString(localizedMessageRes))
        }
    }

    @Throws(IOException::class)
    private fun throwException(message: String, localizedMessage: String) {
        val myException = MyException(message)
        myException.setLocalizedMessage(localizedMessage)
        throw myException
    }
}