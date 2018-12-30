package com.chadrc.hangman

import models.Game
import kotlin.test.*

class HangmanDatabaseTest {
    private val database = HangmanDatabase()
    private val utils = TestUtils()

    @Test
    fun createWord() {
        utils.basicDataSetup()
        val word = database.createWord("brown")

        assertNotEquals(-1, word.id)
        assertEquals("brown", word.word)
    }

    @Test
    fun getRandomWord() {
        utils.basicDataSetup()

        val word = database.getRandomWord()!!
        val knownWords = listOf("panda", "polar", "grizzly")
        assert(knownWords.contains(word.word))
    }

    @Test
    fun getRandomWordWithNoWords() {
        utils.basicDataSetup()
        utils.emptyWords()

        val word = database.getRandomWord()
        assertNull(word)
    }

    @Test
    fun getWordWithString() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        assertEquals("panda", word.word)
    }

    @Test
    fun getNonExistentWord() {
        utils.basicDataSetup()

        val word = database.getWord("black")
        assertNull(word)
    }

    @Test
    fun getWordById() {
        utils.basicDataSetup()

        val createdWord = database.createWord("brown")

        val word = database.getWordById(createdWord.id)!!

        assertEquals(createdWord.id, word.id)
        assertEquals("brown", word.word)
    }

    @Test
    fun createGame() {
        utils.basicDataSetup()

        val randomWord = database.getRandomWord()!!
        val game: Game = database.createGame(randomWord.id, 10)

        assertNotEquals(-1, game.id)
        assertEquals(randomWord.id, game.wordId)
        assertEquals(10, game.guessesAllowed)

        // check database for created game
        val statement = utils.connection.prepareStatement("""
            SELECT * FROM games WHERE id=?
        """.trimIndent())

        statement.setInt(1, game.id)

        val resultSet = statement.executeQuery()

        assertTrue(resultSet.next())

        assertEquals(randomWord.id, resultSet.getInt("word_id"))
        assertEquals(10, resultSet.getInt("guesses_allowed"))

        resultSet.close()
    }

    @Test
    fun getGame() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        val createdGame = database.createGame(word.id, 15)

        val game = database.getGame(createdGame.id) ?: throw Exception("Game doesn't exist")

        assertEquals(createdGame.id, game.id)
        assertEquals(createdGame.wordId, game.wordId)
        assertEquals(createdGame.guessesAllowed, game.guessesAllowed)
    }

    @Test
    fun getNonexistentGame() {
        utils.basicDataSetup()

        val game = database.getGame(9)
        assertNull(game)
    }

    @Test
    fun createGuess() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        val guess = database.createGuess(game.id, 'r')

        assertNotEquals(-1, guess.id)
        assertEquals('r', guess.guess)
    }

    @Test
    fun getGuesses() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        database.createGuess(game.id, 'r')
        database.createGuess(game.id, 't')
        database.createGuess(game.id, 's')
        database.createGuess(game.id, 'e')

        val guesses = database.getGuessesWithGameId(game.id)

        assertEquals(4, guesses.size)
    }

    @Test
    fun getNoGuesses() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        val guesses = database.getGuessesWithGameId(game.id)

        assertEquals(0, guesses.size)
    }

    @Test
    fun createWonGameResult() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        val gameResult = database.createWonGameResult(game.id, true)

        assertEquals(game.id, gameResult.gameId)
        assertNull(gameResult.forfeit)
        assertEquals(true, gameResult.won)

        val resultSet = utils.connection.prepareStatement("""
            SELECT * FROM game_results WHERE id=${gameResult.id}
        """.trimIndent()).executeQuery()

        assertTrue(resultSet.next())

        assertEquals(gameResult.id, resultSet.getInt("id"))
        assertEquals(game.id, resultSet.getInt("game_id"))
        assertEquals(true, resultSet.getBoolean("won"))
        assertNull(resultSet.getObject("forfeit"))

        resultSet.close()
    }

    @Test
    fun createForfeitGameResult() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        val gameResult = database.createForfeitGameResult(game.id, true)

        assertEquals(game.id, gameResult.gameId)
        assertEquals(true, gameResult.forfeit)
        assertNull(gameResult.won)

        val resultSet = utils.connection.prepareStatement("""
            SELECT * FROM game_results WHERE id=${gameResult.id}
        """.trimIndent()).executeQuery()

        assertTrue(resultSet.next())

        assertEquals(gameResult.id, resultSet.getInt("id"))
        assertNull(resultSet.getObject("won"))
        assertEquals(game.id, resultSet.getInt("game_id"))
        assertEquals(true, resultSet.getBoolean("forfeit"))

        resultSet.close()
    }

    @Test
    fun getGameResult() {
        utils.basicDataSetup()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        database.createForfeitGameResult(game.id, true)

        val gameResult = database.getGameResultWithGameId(game.id)

        assertNotNull(gameResult)
        assertTrue(gameResult.forfeit!!)
        assertNull(gameResult.won)
    }
}