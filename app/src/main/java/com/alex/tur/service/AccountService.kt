package com.alex.tur.service

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.alex.tur.ui.login.AbsAuthActivity
import com.alex.tur.utils.ThreadUtils
import dagger.android.DaggerService
import timber.log.Timber


class AccountService : DaggerService() {

    lateinit var mAuthenticator: Authenticator

    override fun onCreate() {
        super.onCreate()
        mAuthenticator = Authenticator(this)
        Timber.d("onCreate %s", mAuthenticator)
    }

    override fun onBind(intent: Intent): IBinder? {
        return mAuthenticator.iBinder
    }







    inner class Authenticator(val context: Context) : AbstractAccountAuthenticator(context) {

        @Throws(NetworkErrorException::class)
        override fun addAccount(response: AccountAuthenticatorResponse,
                                accountType: String,
                                authTokenType: String?,
                                requiredFeatures: Array<String>?,
                                options: Bundle): Bundle? {
            Timber.d("addAccount accountType: %s, authTokenType: %s, requiredFeatures: %s",
                    accountType, authTokenType, requiredFeatures)
            for (key in options.keySet()) {
                Timber.d( "addAccount options %s, %s", key, options.get(key))
            }





            val result = Bundle()
            val intent = Intent(context, AbsAuthActivity::class.java)
            intent.putExtra(AbsAuthActivity.EXTRA_TOKEN_TYPE, authTokenType)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

            result.putAll(options)
            result.putParcelable(AccountManager.KEY_INTENT, intent)
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun getAuthToken(response: AccountAuthenticatorResponse,
                                  account: Account, authTokenType: String, options: Bundle): Bundle? {
            Timber.tag("testsync").d("AccountService getAuthToken %s, %s", ThreadUtils.isMainThread(), Thread.currentThread().hashCode())
            Timber.d("getAuthToken account: %s, authTokenType: %s",
                    account, authTokenType)
            for (key in options.keySet()) {
                Timber.d( "getAuthToken options %s, %s", key, options.get(key))
            }





            val result = Bundle()
            val am = AccountManager.get(context)
            val authToken = am.peekAuthToken(account, authTokenType)
            if (authToken.isNullOrEmpty()) {
                Timber.d("getAuthToken empty token")
                Timber.tag("testsync").d("AccountService getAuthToken login")
                //authRepository.login("", "")
//                val password = am.getPassword(account)
//                if (!TextUtils.isEmpty(password)) {
//                    authToken = AuthTokenLoader.signIn(mContext, account.name, password)
//                }
            }

            if (!authToken.isNullOrEmpty()) {
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            } else {
                val intent = Intent(context, AbsAuthActivity::class.java)
                intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                intent.putExtra(AbsAuthActivity.EXTRA_TOKEN_TYPE, authTokenType)
//                result.putParcelable(AccountManager.KEY_INTENT, intent)
            }
            return null
        }








        override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle? {
            Timber.d("editProperties accountType: %s", accountType)
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle?): Bundle? {
            Timber.d("confirmCredentials ")
            return null
        }

        override fun getAuthTokenLabel(authTokenType: String): String? {
            Timber.d("getAuthTokenLabel authTokenType: %s", authTokenType)
            return "getAuthTokenLabel"
        }

        @Throws(NetworkErrorException::class)
        override fun updateCredentials(response: AccountAuthenticatorResponse,
                                       account: Account, authTokenType: String?, options: Bundle?): Bundle {
            Timber.d("updateCredentials account: %s, authTokenType: %s",
                    account, authTokenType)
            if (options != null) {
                for (key in options.keySet()) {
                    Timber.d( "updateCredentials options %s, %s", key, options.get(key))
                }
            }

            throw UnsupportedOperationException()
        }

        @Throws(NetworkErrorException::class)
        override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<String>): Bundle {
            Timber.d("hasFeatures account: %s, features: %s", account, features)
            throw UnsupportedOperationException()
        }
    }
}
