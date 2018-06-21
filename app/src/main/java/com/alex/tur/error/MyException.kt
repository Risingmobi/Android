package com.alex.tur.error

import timber.log.Timber
import java.io.IOException

open class MyException : IOException {

    constructor() {}

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)



    var localMessage: String? = null

    fun setLocalizedMessage(localMessage: String) {
        Timber.d("setLocalizedMessage %s", localMessage)
        this.localMessage = localMessage
    }

    override fun getLocalizedMessage(): String? {
        Timber.d("getLocalizedMessage %s", localMessage)
        return localMessage
    }
}