package com.chadrc.hangman

import models.GameInfo
import models.Guess

class HangmanService {
    private val database = HangmanDatabase()

    fun startGame(): Result<GameInfo> {
        val word = database.getRandomWord() ?: return Error("Game not found")
        val game = database.createGame(word.id, 10)
        return Ok(GameInfo(game))
    }

    fun getGame(id: Int): GameInfo? {
        val game = database.getGame(id) ?: return null
        val guesses = database.getGuessesWithGameId(id)
        val result = database.getGameResultWithGameId(id)

        return GameInfo(game, guesses, result)
    }

    fun makeGuess(gameId: Int, guess: Char): Result<Guess> {
        database.getGame(gameId) ?: throw Exception("Game not found")

        return Ok(database.createGuess(gameId, guess))
    }
}