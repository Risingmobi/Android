package com.alex.tur.client.datamanager.auth

import android.arch.lifecycle.LiveData
import com.alex.tur.helper.Result
import com.alex.tur.model.api.RequestLogin
import com.alex.tur.model.api.RequestSignUp

interface AuthDataManager {
    fun signUp(requestSignUp: RequestSignUp): LiveData<Result<Boolean>>
    fun login(requestLogin: RequestLogin): LiveData<Result<Boolean>>
}