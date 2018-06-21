package com.alex.tur.helper

import android.content.Context
import android.util.Patterns
import com.alex.tur.AppConstants
import com.alex.tur.R
import com.alex.tur.di.qualifier.global.QualifierAppContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Validator @Inject constructor(@QualifierAppContext private val context: Context) {

    fun validateName(name: String?): Result<Unit> {
        if (name.isNullOrEmpty()) {
            return Result.errorFromLocal(context.getString(R.string.empty_name))
        } else {
            return Result.successFromLocal(Unit)
        }
    }

    fun validatePassword(password: String?): Result<Unit> {
        if (password.isNullOrEmpty()) {
            return Result.errorFromLocal(context.getString(R.string.empty_password))
        }
        if (password!!.length < AppConstants.PASSWORD_LENGTH) {
            return Result.errorFromLocal(context.getString(R.string.short_password))
        }
        return Result.successFromLocal(Unit)
    }

    fun validateEmail(email: String?): Result<Unit> {
        if (email.isNullOrEmpty()) {
            return Result.errorFromLocal(context.getString(R.string.empty_email))
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.errorFromLocal(context.getString(R.string.invalid_email))
        }
        return Result.successFromLocal(Unit)
    }
}