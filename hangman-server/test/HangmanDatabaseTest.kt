package com.chadrc.hangman

import models.Game
import kotlin.test.*

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import kotlin.Exception

class HangmanDatabaseTest {
    private val connection: Connection
        get() = DriverManager.getConnection(
            "jdbc:postgresql://localhost/postgres?user=postgres&password=password"
        )

    private val database = HangmanDatabase()

    @Test
    fun getRandomWord() {
        setUp()

        val word = database.getRandomWord()!!
        val knownWords = listOf("panda", "polar", "grizzly")
        assert(knownWords.contains(word.word))
    }

    @Test
    fun getRandomWordWithNoWords() {
        setUp()
        emptyWords()

        val word = database.getRandomWord()
        assertNull(word)
    }

    @Test
    fun getWordWithString() {
        setUp()

        val word = database.getWord("panda")!!
        assertEquals("panda", word.word)
    }

    @Test
    fun getNonExistentWord() {
        setUp()

        val word = database.getWord("black")
        assertNull(word)
    }

    @Test
    fun createGame() {
        setUp()

        val randomWord = database.getRandomWord()!!
        val game: Game = database.createGame(randomWord.id, 10)

        assertNotEquals(-1, game.id)
        assertEquals(randomWord.id, game.wordId)
        assertEquals(10, game.guessesAllowed)

        // check database for created game
        val statement = connection.prepareStatement("""
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
        setUp()

        val word = database.getWord("panda")!!
        val createdGame = database.createGame(word.id, 15)

        val game = database.getGame(createdGame.id) ?: throw Exception("Game doesn't exist")

        assertEquals(createdGame.id, game.id)
        assertEquals(createdGame.wordId, game.wordId)
        assertEquals(createdGame.guessesAllowed, game.guessesAllowed)
    }

    @Test
    fun getNonexistentGame() {
        setUp()

        val game = database.getGame(9)
        assertNull(game)
    }

    @Test
    fun createGuess() {
        setUp()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        val guess = database.createGuess(game.id, 'r')

        assertNotEquals(-1, guess.id)
        assertEquals('r', guess.guess)
    }

    @Test
    fun getGuesses() {
        setUp()

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
    fun createWonGameResult() {
        setUp()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        val gameResult = database.createWonGameResult(game.id, true)

        assertEquals(game.id, gameResult.gameId)
        assertNull(gameResult.forfeit)
        assertEquals(true, gameResult.won)

        val resultSet = connection.prepareStatement("""
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
        setUp()

        val word = database.getWord("panda")!!
        val game = database.createGame(word.id, 10)

        val gameResult = database.createForfeitGameResult(game.id, true)

        assertEquals(game.id, gameResult.gameId)
        assertEquals(true, gameResult.forfeit)
        assertNull(gameResult.won)

        val resultSet = connection.prepareStatement("""
            SELECT * FROM game_results WHERE id=${gameResult.id}
        """.trimIndent()).executeQuery()

        assertTrue(resultSet.next())

        assertEquals(gameResult.id, resultSet.getInt("id"))
        assertNull(resultSet.getObject("won"))
        assertEquals(game.id, resultSet.getInt("game_id"))
        assertEquals(true, resultSet.getBoolean("forfeit"))

        resultSet.close()
    }

    private fun setUp() {
        emptyGameResults()
        emptyGuesses()
        emptyGames()
        emptyWords()
        addTestWords()
    }

    private fun addTestWords() {
        val conn = connection

        val statement = conn.createStatement()
        statement.executeUpdate(
            """
            INSERT INTO words (word)
                VALUES  ('panda'),
                        ('grizzly'),
                        ('polar');
                """
        )

        statement.close()
    }

    private fun emptyGameResults() = emptyTable("game_results")

    private fun emptyGuesses() = emptyTable("guesses")

    private fun emptyGames() = emptyTable("games")

    private fun emptyWords() = emptyTable("words")

    private fun emptyTable(table: String) {
        val statement: Statement = connection.createStatement()
        @Suppress("SqlWithoutWhere")
        statement.executeUpdate("DELETE FROM $table")

        statement.close()
    }
}