package com.chadrc.hangman

import models.GameInfo

class HangmanService {
    private val database = HangmanDatabase()

    fun startGame(): GameInfo {
        val word = database.getRandomWord() ?: throw Exception("No words available")
        val game = database.createGame(word.id, 10)
        return GameInfo(game)
    }

    fun getGame(id: Int): GameInfo? {
        val game = database.getGame(id) ?: return null
        val guesses = database.getGuessesWithGameId(id)
        val result = database.getGameResultWithGameId(id)

        return GameInfo(game, guesses, result)
    }
}