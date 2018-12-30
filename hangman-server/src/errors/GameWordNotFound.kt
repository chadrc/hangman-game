package com.chadrc.hangman.errors

import com.chadrc.hangman.Error

class GameWordNotFound<T>(gameId: Int) : Error<T>("Word on Game ($gameId) not found")