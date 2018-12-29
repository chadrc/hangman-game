package com.chadrc.hangman

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

//    @Test
//    fun createGame() {
//        emptyWords()
//        addTestWords()
//
//        database.createGame(10)
//    }

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

    private fun setUp() {
        emptyWords()
        addTestWords()
    }

    private fun addTestWords() {
        val conn = connection

        val statement: Statement = conn.createStatement()
        statement.executeUpdate(
            """
            INSERT INTO words (word)
                VALUES  ('panda'),
                        ('grizzly'),
                        ('polar');
                """
        )
    }

    private fun emptyWords() {
        val conn = connection

        val statement: Statement = conn.createStatement()
        statement.executeUpdate("DELETE FROM words WHERE word IS NOT NULL")
    }
}