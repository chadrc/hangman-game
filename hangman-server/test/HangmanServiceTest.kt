package com.chadrc.hangman

import kotlin.test.*

class HangmanServiceTest {
    private val hangmanService = HangmanService()
    private val utils = TestUtils()

    @Test
    fun startGame() {
        utils.basicDataSetup()

        val gameInfo = hangmanService.startGame()

        assertEquals(10, gameInfo.game.guessesAllowed)
        assertEquals(0, gameInfo.guesses.size)
        assertNull(gameInfo.result)
    }

    @Test
    fun getGame() {
        utils.basicDataSetup()

        val createdGameInfo = hangmanService.startGame()

        val gameInfo = hangmanService.getGame(createdGameInfo.game.id)

        assertNotNull(gameInfo)
        assertEquals(0, gameInfo.guesses.size)
        assertNull(gameInfo.result)
    }

    @Test
    fun makeGuess() {
        utils.basicDataSetup()

        val gameInfo = hangmanService.startGame()

        val guessResult = hangmanService.makeGuess(gameInfo.game.id, 'c')

        assertEquals('c', guessResult.guess)
    }
}