package com.example.imagegallery.data.common

sealed class Result<out R> {
    data class Success<out T>(val data:T) : Result<T>()
    data class Error(val errorMsg:String) : Result<Nothing>()
    object Unauthorized : Result<Nothing>()

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data]"
        is Error -> "Error[exception=$errorMsg]"
        is Unauthorized -> "Error[exception=Unauthorized]"
    }
}

val Result<*>.isSuccess
    get() = this is Result.Success

val Result<*>.isUnauthorized
    get() = this is Result.Unauthorized

fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data

fun <T> Result<T>.getErrorMsg(): String = (this as? Result.Error)?.errorMsg ?: "Unknown error"