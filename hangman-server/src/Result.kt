package com.chadrc.hangman

abstract class Result<T>(private val obj: T? = null) {
    fun result(): T? = obj
}

class Ok<T>(obj: T) : Result<T>(obj)

class Error<T>(val message: String = ""): Result<T>(null)