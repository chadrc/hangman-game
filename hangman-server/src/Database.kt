package com.chadrc.hangman

import models.Word
import java.sql.Connection
import java.sql.DriverManager

class Database {
    private val connection: Connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost/postgres?user=postgres&password=password"
    )

    fun createGame(wordId: Int, guessesAllowed: Int) {
        val statement = connection.prepareStatement("""
            INSERT INTO games (
        """.trimIndent())


    }

    fun getRandomWord(): Word {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("""
            SELECT * FROM words OFFSET floor(random()*(SELECT COUNT(*) FROM words)) LIMIT 1
        """.trimIndent())

        resultSet.next()

        val id = resultSet.getInt("id")
        val word = resultSet.getString("word")

        resultSet.close()
        statement.close()

        return Word(id, word)
    }
}