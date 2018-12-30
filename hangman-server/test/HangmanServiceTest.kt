package com.chadrc.hangman

import com.chadrc.hangman.errors.GameAlreadyCompleteError
import com.chadrc.hangman.errors.GameNotFoundError
import com.chadrc.hangman.errors.NoWordsAvailableError
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
        assertTrue(gameInfoResult is NoWordsAvailableError)
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
    fun `Getting a game that does not exist returns an Error`() {
        utils.basicDataSetup()

        val getGameResult = hangmanService.getGame(10)

        assertTrue(getGameResult is GameNotFoundError)
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
    fun `Making a guess on game that does not exist returns an Error`() {
        utils.basicDataSetup()

        val guessResult = hangmanService.makeGuess(10, 'c')

        assertTrue(guessResult is GameNotFoundError)
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

    @Test
    fun `Make guess on game that is already complete returns Error`() {
        utils.basicDataSetup()

        val startGameResult = hangmanService.startGame() as Ok
        val game = startGameResult.result().game

        for (c in listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')) {
            hangmanService.makeGuess(game.id, c)
        }

        val guessResult = hangmanService.makeGuess(game.id, 'k')

        assertTrue(guessResult is GameAlreadyCompleteError)
    }
}