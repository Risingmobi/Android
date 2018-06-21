package com.alex.tur.driver.datamanager.auth

import android.arch.lifecycle.LiveData
import com.alex.tur.helper.Result
import com.alex.tur.model.api.RequestLogin

interface DriverAuthDataManager {
    fun  login(requestLogin: RequestLogin): LiveData<Result<Unit>>
}