package com.chadrc.hangman.errors

import com.chadrc.hangman.Error

class NoWordsAvailableError<T> : Error<T>("No words available")