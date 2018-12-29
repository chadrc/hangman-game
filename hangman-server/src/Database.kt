package com.chadrc.hangman

import models.Word
import java.sql.*

class Database {
    private val connection: Connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost/postgres?user=postgres&password=password"
    )

    fun createGame(wordId: Int, guessesAllowed: Int) {
        val statement = connection.prepareStatement("""
            INSERT INTO games (
        """.trimIndent())


    }

    fun getWord(word: String): Word {
        val statement = connection.prepareStatement("""
            SELECT * FROM words WHERE word=?
        """.trimIndent())

        statement.setString(1, word)

        return executeAndGetFirstWord(statement)
    }

    fun getRandomWord(): Word {
        val statement = connection.prepareStatement("""
            SELECT * FROM words OFFSET floor(random()*(SELECT COUNT(*) FROM words)) LIMIT 1
        """.trimIndent())

        return executeAndGetFirstWord(statement)
    }

    private fun executeAndGetFirstWord(statement: PreparedStatement): Word {
        val resultSet = statement.executeQuery()

        val wordObj = makeWordFromNextResultSet(resultSet)

        resultSet.close()
        statement.close()

        return wordObj
    }

    private fun makeWordFromNextResultSet(resultSet: ResultSet): Word {
        resultSet.next()

        val id = resultSet.getInt("id")
        val word = resultSet.getString("word")

        return Word(id, word)
    }
}