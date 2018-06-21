package com.alex.tur.helper

import java.io.Serializable

class Result<T>
private constructor(
        val type: Type,
        val status: Status,
        var data: T?,
        val message: String?,
        val isInitial: Boolean = false,
        val hasError: Boolean = false
): Serializable {

    companion object {

        fun <T> successFromLocal(data: T?): Result<T> {
            return Result(Type.LOCAL, Status.SUCCESS, data, null)
        }

        fun <T> successFromRemote(data: T?): Result<T> {
            return Result(Type.REMOTE, Status.SUCCESS, data, null)
        }

        fun <T> errorFromLocal(message: String?, data: T? = null): Result<T> {
            return Result(Type.LOCAL, Status.ERROR, data, message, false, true)
        }

        fun <T> errorFromRemote(message: String?, data: T? = null): Result<T> {
            return Result(Type.REMOTE, Status.ERROR, data, message, false, true)
        }

        fun <T> loading(result: Result<T>? = null): Result<T> {
            return if (result != null) {
                Result(result.type, Status.LOADING, result.data, result.message, false, result.hasError)
            } else {
                Result(Type.LOCAL, Status.LOADING, null, null)
            }
        }

        fun <T> initialLoading(): Result<T> {
            return Result(Type.LOCAL, Status.LOADING, null, null, true)
        }

        fun <T> loading(data: T): Result<T> {
            return Result(Type.LOCAL, Status.LOADING, data, null)
        }
    }

    fun reSetData(data: T?): Result<T> {
        this.data = data
        return this
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    enum class Type {
        LOCAL,
        REMOTE
    }


    override fun toString(): String {
        return "Result(type=$type, status=$status, isInitial=$isInitial, hasError=$hasError, message=$message, data=$data)"
    }
}