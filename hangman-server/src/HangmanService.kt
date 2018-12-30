package com.chadrc.hangman

import models.GameInfo

class HangmanService {
    private val database = HangmanDatabase()

    fun startGame(): Result<GameInfo> {
        val word = database.getRandomWord() ?: return Error("No words available")
        val game = database.createGame(word.id, 10)
        return Ok(GameInfo(game))
    }

    fun getGame(id: Int): Result<GameInfo> {
        val game = database.getGame(id) ?: return Error("Game ($id) not found")
        val guesses = database.getGuessesWithGameId(id)
        val result = database.getGameResultWithGameId(id)

        return Ok(GameInfo(game, guesses, result))
    }

    fun makeGuess(gameId: Int, guess: Char): Result<GameInfo> {
        val game = database.getGame(gameId) ?: return Error("Game ($gameId) not found")

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
        val result = if (guessedAllLetters) {
            database.createWonGameResult(game.id, true)
        } else { null }

        return Ok(GameInfo(game, guesses, result))
    }
}