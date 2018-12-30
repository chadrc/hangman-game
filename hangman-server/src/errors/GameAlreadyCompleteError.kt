package com.chadrc.hangman.errors

import com.chadrc.hangman.Error

class GameAlreadyCompleteError<T>(gameId: Int) : Error<T>("Game ($gameId) already complete")