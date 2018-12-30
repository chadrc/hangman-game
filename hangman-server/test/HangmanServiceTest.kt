package com.chadrc.hangman

import models.GameInfo
import kotlin.test.*

class HangmanServiceTest {
    private val hangmanService = HangmanService()
    private val hangmanDatabase = HangmanDatabase()
    private val utils = TestUtils()

    @Test
    fun startGame() {
        utils.basicDataSetup()

        val startGameResult = hangmanService.startGame() as Ok

        assertEquals(10, startGameResult.result().game.guessesAllowed)
        assertEquals(0, startGameResult.result().guesses.size)
        assertNull(startGameResult.result().result)
    }

    @Test
    fun `Starting game with no words available returns Error`() {
        utils.emptyAll()
        val gameInfoResult = hangmanService.startGame()
        assertTrue(gameInfoResult is Error)
    }

    @Test
    fun getGame() {
        utils.basicDataSetup()

        val startGameResult = hangmanService.startGame() as Ok

        val getGameResult = hangmanService.getGame(startGameResult.result().game.id) as Ok

        assertNotNull(getGameResult)
        assertEquals(0, getGameResult.result().guesses.size)
        assertNull(getGameResult.result().result)
    }

    @Test
    fun makeGuess() {
        utils.basicDataSetup()

        val startGameResult = hangmanService.startGame() as Ok

        val guessResult = hangmanService.makeGuess(startGameResult.result().game.id, 'c')

        if (guessResult is Ok) {
            assertNotNull(guessResult.result().guesses.find { it.guess == 'c' })
            assertNull(guessResult.result().result)
        } else {
            fail((guessResult as Error).message)
        }
    }

    @Test
    fun makeGuessesUntilWon() {
        utils.basicDataSetup()

        val startGameResult = hangmanService.startGame() as Ok
        val game = startGameResult.result().game

        val word = hangmanDatabase.getWordById(game.wordId)!!

        var guessResult: GameInfo? = null
        for (c in word.word) {
            guessResult = (hangmanService.makeGuess(game.id, c) as Ok).result()
        }

        assertNotNull(guessResult?.result)
        assertTrue(guessResult?.result?.won!!)
    }

    @Test
    fun makeGuessesUntilLost() {
        utils.basicDataSetup()

        val startGameResult = hangmanService.startGame() as Ok
        val game = startGameResult.result().game

        var guessResult: GameInfo? = null
        for (c in listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')) {
            guessResult = (hangmanService.makeGuess(game.id, c) as Ok).result()
        }

        assertNotNull(guessResult?.result)
        assertFalse(guessResult?.result?.won!!)
    }
}