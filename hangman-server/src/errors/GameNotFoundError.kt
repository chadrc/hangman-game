package com.chadrc.hangman.errors

import com.chadrc.hangman.Error

class GameNotFoundError<T>(gameId: Int) : Error<T>("Game ($gameId) not found")
