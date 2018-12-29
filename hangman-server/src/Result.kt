package com.chadrc.hangman

abstract class Result<T>(private val obj: T) {
    fun result(): T = obj
}

class Ok<T>(obj: T) : Result<T>(obj)

class Error(val message: String = ""): Result<Nothing?>(null)