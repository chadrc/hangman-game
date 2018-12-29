package com.chadrc.hangman

import models.GameInfo
import models.Guess

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

    fun makeGuess(gameId: Int, guess: Char): Guess {
        database.getGame(gameId) ?: throw Exception("Game not found")

        return database.createGuess(gameId, guess)
    }
}