package com.chadrc.hangman

import com.chadrc.hangman.errors.GameAlreadyCompleteError
import com.chadrc.hangman.errors.GameNotFoundError
import com.chadrc.hangman.errors.NoWordsAvailableError
import models.GameInfo
import org.junit.After
import org.junit.Before
import kotlin.test.*

class HangmanServiceTest {
    private val hangmanService = HangmanService()
    private val hangmanDatabase = HangmanDatabase()
    private val utils = TestUtils()

    @Before
    fun setUp() {
        utils.basicDataSetup()
    }

    @After
    fun cleanUp() {
        utils.emptyAll()
        hangmanDatabase.close()
        utils.close()
    }

    @Test
    fun startGame() {
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
        val startGameResult = hangmanService.startGame() as Ok

        val getGameResult = hangmanService.getGame(startGameResult.result().game.id) as Ok

        assertNotNull(getGameResult)
        assertEquals(0, getGameResult.result().guesses.size)
        assertNull(getGameResult.result().result)
    }

    @Test
    fun `Getting a game that does not exist returns an Error`() {
        val getGameResult = hangmanService.getGame(10)

        assertTrue(getGameResult is GameNotFoundError)
    }

    @Test
    fun makeGuess() {
        val startGameResult = hangmanService.startGame() as Ok

        val guessResult = hangmanService.makeGuess(startGameResult.result().game.id, 'c')

        if (guessResult is Ok) {
            assertNotNull(guessResult.result().guesses.find { it.guess == "c" })
            assertNull(guessResult.result().result)
        } else {
            fail((guessResult as Error).message)
        }
    }

    @Test
    fun `Making a guess on game that does not exist returns an Error`() {
        val guessResult = hangmanService.makeGuess(10, 'c')

        assertTrue(guessResult is GameNotFoundError)
    }

    @Test
    fun makeGuessesUntilWon() {
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
        val startGameResult = hangmanService.startGame() as Ok
        val game = startGameResult.result().game

        for (c in listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')) {
            hangmanService.makeGuess(game.id, c)
        }

        val guessResult = hangmanService.makeGuess(game.id, 'k')

        assertTrue(guessResult is GameAlreadyCompleteError)
    }

    @Test
    fun `Make correct word guess`() {
        val startGameResult = hangmanService.startGame() as Ok

        val word = hangmanDatabase.getWordById(startGameResult.result().game.wordId)!!

        val guessResult = hangmanService.makeWordGuess(startGameResult.result().game.id, word.word) as Ok

        assertNotNull(guessResult.result().guesses.find { it.guess == word.word })
        assertNotNull(guessResult.result().result)
        assertTrue(guessResult.result().result?.won!!)
    }

    @Test
    fun `Make incorrect word guess`() {
        val startGameResult = hangmanService.startGame() as Ok

        val guessResult = hangmanService.makeWordGuess(startGameResult.result().game.id, "brown") as Ok

        assertNotNull(guessResult.result().guesses.find { it.guess == "brown" })
        assertNull(guessResult.result().result)
    }

    @Test
    fun `Making character and word guesses results in guesses list with all guesses`() {
        val startGameResult = hangmanService.startGame() as Ok

        hangmanService.makeGuess(startGameResult.result().game.id, 'b')
        hangmanService.makeGuess(startGameResult.result().game.id, 'l')
        hangmanService.makeGuess(startGameResult.result().game.id, 'n')
        hangmanService.makeWordGuess(startGameResult.result().game.id, "black")

        val result = hangmanService.makeWordGuess(startGameResult.result().game.id, "brown") as Ok

        assertEquals(5, result.result().guesses.size)
    }

    @Test
    fun `Making character and word guesses results in guesses list with all guesses, character guess last`() {
        val startGameResult = hangmanService.startGame() as Ok

        hangmanService.makeGuess(startGameResult.result().game.id, 'b')
        hangmanService.makeGuess(startGameResult.result().game.id, 'l')
        hangmanService.makeWordGuess(startGameResult.result().game.id, "black")
        hangmanService.makeWordGuess(startGameResult.result().game.id, "brown")

        val result = hangmanService.makeGuess(startGameResult.result().game.id, 'n') as Ok

        assertEquals(5, result.result().guesses.size)
    }

    @Test
    fun `Get game with both character and word guesses returns all guesses`() {
        val startGameResult = hangmanService.startGame() as Ok

        hangmanService.makeGuess(startGameResult.result().game.id, 'b')
        hangmanService.makeGuess(startGameResult.result().game.id, 'l')
        hangmanService.makeGuess(startGameResult.result().game.id, 'n')
        hangmanService.makeWordGuess(startGameResult.result().game.id, "black")
        hangmanService.makeWordGuess(startGameResult.result().game.id, "brown")

        val getGameResult = hangmanService.getGame(startGameResult.result().game.id) as Ok

        assertEquals(5, getGameResult.result().guesses.size)
    }

    @Test
    fun `Forfeiting has result on game info`() {
        val startGameResult = hangmanService.startGame() as Ok
        val game = startGameResult.result().game

        val forfeitResult = hangmanService.forfeitGame(game.id) as Ok
        val info = forfeitResult.result()

        assertNotNull(info.result)
        assertTrue(info.result?.forfeit!!)
    }

    @Test
    fun `Forfeiting game that is does not exists returns Error`() {
        val forfeitResult = hangmanService.forfeitGame(10)

        assertTrue(forfeitResult is GameNotFoundError)
    }

    @Test
    fun `Forfeiting game that is already complete returns Error`() {
        val startGameResult = hangmanService.startGame() as Ok
        val game = startGameResult.result().game

        hangmanService.forfeitGame(game.id) as Ok
        val forfeitResult = hangmanService.forfeitGame(game.id)

        assertTrue(forfeitResult is GameAlreadyCompleteError)
    }
}