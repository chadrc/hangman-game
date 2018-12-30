package com.chadrc.hangman

import com.chadrc.hangman.errors.GameAlreadyCompleteError
import com.chadrc.hangman.errors.GameNotFoundError
import com.chadrc.hangman.errors.NoWordsAvailableError
import com.chadrc.hangman.errors.GameWordNotFound
import models.GameInfo
import models.Guess

class HangmanService {
    private val database = HangmanDatabase()

    fun startGame(): Result<GameInfo> {
        val word = database.getRandomWord() ?: return NoWordsAvailableError()
        val game = database.createGame(word.id, 10)
        return Ok(GameInfo(game, word.word))
    }

    fun getGame(id: Int): Result<GameInfo> {
        val game = database.getGame(id) ?: return GameNotFoundError(id)
        val guesses = getAllGuessesForGameId(id)
        val result = database.getGameResultWithGameId(id)
        val word = database.getWordById(game.wordId) ?: return GameWordNotFound(id)

        return Ok(GameInfo(game, word.word, guesses, result))
    }

    fun makeGuess(gameId: Int, guess: Char): Result<GameInfo> {
        val game = database.getGame(gameId) ?: return GameNotFoundError(gameId)

        val gameResult = database.getGameResultWithGameId(game.id)

        if (gameResult != null) {
            return GameAlreadyCompleteError(game.id)
        }

        val word = database.getWordById(game.wordId) ?: return GameWordNotFound(gameId)

        val currentGuesses = database.getGuessesWithGameId(gameId)

        if (currentGuesses.find { it.guess == guess.toString() } != null) {
            // No change to game state
            return Ok(GameInfo(game, word.word, currentGuesses))
        }

        database.createGuess(gameId, guess)

        val guesses = getAllGuessesForGameId(gameId)

        val characterGuesses = guesses.filter { it.guess.length == 1 }

        // Check if every letter in the game's word has been guessed
        var guessedAllLetters = true
        word.word.forEach { c ->
            if (characterGuesses.find { it.guess == c.toString() } == null) {
                guessedAllLetters = false
            }
        }

        // Game is won if all letters are guessed
        // Game is lost if maximum guesses has been reached
        val result = when {
            guessedAllLetters -> database.createWonGameResult(game.id, true)
            characterGuesses.size >= game.guessesAllowed -> database.createWonGameResult(game.id, false)
            else -> null
        }

        return Ok(GameInfo(game, word.word, guesses, result))
    }

    fun makeWordGuess(gameId: Int, guess: String): Result<GameInfo> {
        val game = database.getGame(gameId) ?: return GameNotFoundError(gameId)

        val currentResult = database.getGameResultWithGameId(gameId)

        if (currentResult != null) {
            return GameAlreadyCompleteError(gameId)
        }

        val word = database.getWordById(game.wordId) ?: return GameWordNotFound(gameId)

        val currentGuesses = database.getWordGuessesByGameId(gameId)

        if (currentGuesses.find { it.guess == guess } != null) {
            return Ok(GameInfo(game, word.word, currentGuesses))
        }

        database.createWordGuess(gameId, guess)

        val guesses = getAllGuessesForGameId(gameId)

        val result = if (word.word == guess) {
            database.createWonGameResult(gameId, true)
        } else { null }

        return Ok(GameInfo(game, word.word, guesses, result))
    }

    fun forfeitGame(gameId: Int): Result<GameInfo> {
        val game = database.getGame(gameId) ?: return GameNotFoundError(gameId)

        val existingResult = database.getGameResultWithGameId(gameId)
        if (existingResult != null) {
            return GameAlreadyCompleteError(gameId)
        }

        val word = database.getWordById(game.wordId) ?: return GameWordNotFound(gameId)

        val guesses = getAllGuessesForGameId(gameId)

        val result = database.createForfeitGameResult(gameId, true)

        return Ok(GameInfo(game, word.word, guesses, result))
    }

    private fun getAllGuessesForGameId(gameId: Int): List<Guess> {
        val characterGuesses = database.getGuessesWithGameId(gameId)
        val wordGuesses = database.getWordGuessesByGameId(gameId)

        val list = mutableListOf<Guess>()

        list.addAll(characterGuesses)
        list.addAll(wordGuesses)

        return list
    }
}