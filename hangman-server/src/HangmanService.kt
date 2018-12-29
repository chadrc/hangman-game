package com.chadrc.hangman

import models.GameInfo

class HangmanService {
    private val database = HangmanDatabase()

    fun startGame(): GameInfo {
        val word = database.getRandomWord() ?: throw Exception("No words available")
        val game = database.createGame(word.id, 10)
        return GameInfo(game)
    }
}