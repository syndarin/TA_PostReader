package com.vtiahotenkov.gorillastestassignment.feature.posts.usecase

sealed class Result<T> {

    data class Success<T>(val value: T) : Result<T>()

    data class Error<T>(val th: Throwable) : Result<T>()
}