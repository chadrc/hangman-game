package com.chadrc.hangman

import kotlin.test.*

class HangmanServiceTest {
    private val hangmanService = HangmanService()
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
    fun startGameWithNoAvailableWords() {
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
            assertEquals('c', guessResult.result().guess)
        } else {
            fail((guessResult as Error).message)
        }
    }
}