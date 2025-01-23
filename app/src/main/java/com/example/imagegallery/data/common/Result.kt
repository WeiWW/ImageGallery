package com.example.imagegallery.data.common

sealed class Result<out R> {
    data class Success<out T>(val data:T) : Result<T>()
    data class Error(val errorMsg:String) : Result<Nothing>()

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data]"
        is Error -> "Error[exception=$errorMsg]"
    }
}

val Result<*>.isSuccess
    get() = this is Result.Success

fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data

fun <T> Result<T>.getErrorMsg(): String = (this as? Result.Error)?.errorMsg ?: "Unknown error"