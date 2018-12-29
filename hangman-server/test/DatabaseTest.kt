package com.chadrc.hangman

import models.Game
import kotlin.test.*

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class DatabaseTest {
    val connection: Connection
        get() = DriverManager.getConnection(
            "jdbc:postgresql://localhost/postgres?user=postgres&password=password"
        )

    val database = Database()

    @Test
    fun getRandomWord() {
        setUp()

        val word = database.getRandomWord()
        val knownWords = listOf("panda", "polar", "grizzly")
        assert(knownWords.contains(word.word))
    }

    @Test
    fun getWordWithString() {
        setUp()

        val word = database.getWord("panda")
        assertEquals("panda", word.word)
    }

    @Test
    fun createGame() {
        setUp()

        val randomWord = database.getRandomWord()
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
        resultSet.next()
        assertEquals(randomWord.id, resultSet.getInt("word_id"))
        assertEquals(10, resultSet.getInt("guesses_allowed"))
    }

    @Test
    fun getGame() {
        setUp()

        val word = database.getWord("panda")
        val createdGame = database.createGame(word.id, 15)

        val game = database.getGame(createdGame.id)

        assertEquals(createdGame.id, game.id)
        assertEquals(createdGame.wordId, game.wordId)
        assertEquals(createdGame.guessesAllowed, game.guessesAllowed)
    }

    @Test
    fun createGuess() {
        setUp()

        val word = database.getWord("panda")
        val game = database.createGame(word.id, 10)

        val guess = database.createGuess(game.id, 'r')

        assertNotEquals(-1, guess.id)
        assertEquals('r', guess.guess)
    }

    private fun setUp() {
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
    }

    private fun emptyGuesses() {
        emptyTable("guesses")
    }

    private fun emptyGames() {
        emptyTable("games")
    }

    private fun emptyWords() {
        emptyTable("words")
    }

    private fun emptyTable(table: String) {
        val statement: Statement = connection.createStatement()
        statement.executeUpdate("DELETE FROM $table WHERE id IS NOT NULL")
    }
}