package com.chadrc.hangman

abstract class Result<T>

class Ok<T>(private val obj: T) : Result<T>() {
    fun result(): T = obj
}

open class Error<T>(val message: String = ""): Result<T>()