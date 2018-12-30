package com.chadrc.hangman

import com.chadrc.hangman.errors.GameNotFoundError
import com.chadrc.hangman.errors.NoWordsAvailableError
import models.GameInfo

class HangmanService {
    private val database = HangmanDatabase()

    fun startGame(): Result<GameInfo> {
        val word = database.getRandomWord() ?: return NoWordsAvailableError()
        val game = database.createGame(word.id, 10)
        return Ok(GameInfo(game))
    }

    fun getGame(id: Int): Result<GameInfo> {
        val game = database.getGame(id) ?: return GameNotFoundError(id)
        val guesses = database.getGuessesWithGameId(id)
        val result = database.getGameResultWithGameId(id)

        return Ok(GameInfo(game, guesses, result))
    }

    fun makeGuess(gameId: Int, guess: Char): Result<GameInfo> {
        val game = database.getGame(gameId) ?: return GameNotFoundError(gameId)

        database.createGuess(gameId, guess)

        val guesses = database.getGuessesWithGameId(gameId)

        val word =
            database.getWordById(game.wordId) ?: return Error("Word (${game.wordId}) on Game (${game.id}) not found")

        // Check if every letter in the game's word has been guessed
        var guessedAllLetters = true
        word.word.forEach { c ->
            if (guesses.find { it.guess == c } == null) {
                guessedAllLetters = false
            }
        }

        // Game is won if all letters are guessed
        // Game is lost if maximum guesses has been reached
        val result = when {
            guessedAllLetters -> database.createWonGameResult(game.id, true)
            guesses.size >= game.guessesAllowed -> database.createWonGameResult(game.id, false)
            else -> null
        }

        return Ok(GameInfo(game, guesses, result))
    }
}