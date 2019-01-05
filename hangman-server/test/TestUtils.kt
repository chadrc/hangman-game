package com.chadrc.hangman

import java.sql.Connection
import java.sql.Statement

class TestUtils {
    private val datasource = makeConnectionPool()

    val newConnection: Connection
        get() {
            return datasource.connection
        }

    fun close() {
        datasource.close()
    }

    fun addTestWords() {
        val conn = datasource.connection

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
        conn.close()
    }

    fun basicDataSetup() {
        emptyAll()
        addTestWords()
    }

    fun emptyAll() {
        emptyGameResults()
        emptyCharacterGuesses()
        emptyWordGuesses()
        emptyGames()
        emptyWords()
    }

    fun emptyGameResults() = emptyTable("game_results")

    fun emptyCharacterGuesses() = emptyTable("character_guesses")

    fun emptyWordGuesses() = emptyTable("word_guesses")

    fun emptyGames() = emptyTable("games")

    fun emptyWords() = emptyTable("words")

    private fun emptyTable(table: String) {
        val conn = datasource.connection
        val statement: Statement = conn.createStatement()
        @Suppress("SqlWithoutWhere")
        statement.executeUpdate("DELETE FROM $table")

        statement.close()
        conn.close()
    }
}