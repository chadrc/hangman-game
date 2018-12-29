package com.chadrc.hangman

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class TestUtils {
    val connection: Connection
        get() = DriverManager.getConnection(
            "jdbc:postgresql://localhost/postgres?user=postgres&password=password"
        )

    fun addTestWords() {
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

    fun basicDataSetup() {
        emptyAll()
        addTestWords()
    }

    fun emptyAll() {
        emptyGameResults()
        emptyGuesses()
        emptyGames()
        emptyWords()
    }

    fun emptyGameResults() = emptyTable("game_results")

    fun emptyGuesses() = emptyTable("guesses")

    fun emptyGames() = emptyTable("games")

    fun emptyWords() = emptyTable("words")

    private fun emptyTable(table: String) {
        val statement: Statement = connection.createStatement()
        @Suppress("SqlWithoutWhere")
        statement.executeUpdate("DELETE FROM $table")

        statement.close()
    }
}