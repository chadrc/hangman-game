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
}