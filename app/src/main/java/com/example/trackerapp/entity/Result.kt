package com.example.trackerapp.entity

sealed class Result<T> {
    suspend fun doOnSuccess(onSuccess: suspend (T) -> Unit) = apply {
        if (this is Success) onSuccess(data)
    }

    suspend fun doOnError(onError: suspend (Throwable) -> Unit) = apply {
        if (this is Error) onError(throwable)
    }

    data class Success<T>(
        val data: T
    ) : Result<T>()

    data class Error<T>(
        val throwable: Throwable
    ) : Result<T>()
}
