package com.chadrc.hangman

import models.Game
import models.GameResult
import models.Guess
import models.Word
import java.sql.*

class HangmanDatabase {
    private val connection: Connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost/postgres?user=postgres&password=password"
    )

    fun createWord(word: String): Word {
        val statement = connection.prepareStatement("""
            INSERT INTO words (word)
            VALUES (?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)

        statement.setString(1, word)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys

        resultSet.next()

        val id = resultSet.getInt("id")

        return Word(id, word)
    }

    fun getWordById(id: Int): Word? {
        val statement = connection.prepareStatement("""
            SELECT * FROM words WHERE id=?
        """.trimIndent())

        statement.setInt(1, id)

        return executeAndGetFirstWord(statement)
    }

    fun createGame(wordId: Int, guessesAllowed: Int): Game {
        val statement = connection.prepareStatement("""
            INSERT INTO games (word_id, guesses_allowed)
            VALUES (?, ?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)

        statement.setInt(1, wordId)
        statement.setInt(2, guessesAllowed)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys

        resultSet.next()

        val id = resultSet.getInt("id")

        resultSet.close()
        statement.close()

        return Game(id, wordId, guessesAllowed)
    }

    fun getGame(gameId: Int): Game? {
        val statement = connection.prepareStatement("""
            SELECT * FROM games WHERE id=?
        """.trimIndent())

        statement.setInt(1, gameId)

        val resultSet = statement.executeQuery()

        if (!resultSet.next()) {
            return null
        }

        val id = resultSet.getInt("id")
        val wordId = resultSet.getInt("word_id")
        val guessesAllowed = resultSet.getInt("guesses_allowed")

        resultSet.close()
        statement.close()

        return Game(id, wordId, guessesAllowed)
    }

    fun getWord(word: String): Word? {
        val statement = connection.prepareStatement("""
            SELECT * FROM words WHERE word=?
        """.trimIndent())

        statement.setString(1, word)

        return executeAndGetFirstWord(statement)
    }

    fun createGuess(gameId: Int, guess: Char): Guess {
        val statement = connection.prepareStatement("""
            INSERT INTO guesses (game_id, guess)
            VALUES (?, ?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)

        statement.setInt(1, gameId)
        statement.setString(2, guess.toString())

        statement.executeUpdate()

        val resultSet = statement.generatedKeys

        resultSet.next()

        val id = resultSet.getInt("id")

        resultSet.close()
        statement.close()

        return Guess(id, gameId, guess)
    }

    fun getRandomWord(): Word? {
        val statement = connection.prepareStatement("""
            SELECT * FROM words OFFSET floor(random()*(SELECT COUNT(*) FROM words)) LIMIT 1
        """.trimIndent())

        return executeAndGetFirstWord(statement)
    }

    fun getGuessesWithGameId(gameId: Int): List<Guess> {
        val statement = connection.prepareStatement("""
            SELECT * FROM guesses WHERE game_id=?
        """.trimIndent())

        statement.setInt(1, gameId)

        val resultSet = statement.executeQuery()

        val guesses = mutableListOf<Guess>()

        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val guessStr = resultSet.getString("guess")

            guesses.add(Guess(id, gameId, guessStr[0]))
        }

        resultSet.close()
        statement.close()

        return guesses
    }

    fun createWonGameResult(gameId: Int, won: Boolean): GameResult {
        val statement = connection.prepareStatement("""
            INSERT INTO game_results (game_id, won)
            VALUES (?, ?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)

        statement.setInt(1, gameId)
        statement.setBoolean(2, won)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys

        resultSet.next()

        val id = resultSet.getInt("id")

        resultSet.close()
        statement.close()

        return GameResult(id, gameId, won)
    }

    fun createForfeitGameResult(gameId: Int, forfeit: Boolean): GameResult {
        val statement = connection.prepareStatement("""
            INSERT INTO game_results (game_id, forfeit)
            VALUES (?, ?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)

        statement.setInt(1, gameId)
        statement.setBoolean(2, forfeit)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys

        resultSet.next()

        val id = resultSet.getInt("id")

        resultSet.close()
        statement.close()

        return GameResult(id, gameId, forfeit = forfeit)
    }

    fun getGameResultWithGameId(gameId: Int): GameResult? {
        val statement = connection.prepareStatement("""
            SELECT * FROM game_results WHERE game_id=?
        """.trimIndent())

        statement.setInt(1, gameId)

        val resultSet = statement.executeQuery()

        if (!resultSet.next()) {
            return null
        }

        val id = resultSet.getInt("id")
        val wonObj = resultSet.getObject("won")
        val won = if (wonObj is Boolean) wonObj else null
        val forfeitObj = resultSet.getObject("forfeit")
        val forfeit = if (forfeitObj is Boolean) forfeitObj else null

        return GameResult(id, gameId, won, forfeit)
    }

    private fun executeAndGetFirstWord(statement: PreparedStatement): Word? {
        val resultSet = statement.executeQuery()

        val wordObj = makeWordFromNextResultSet(resultSet)

        resultSet.close()
        statement.close()

        return wordObj
    }

    private fun makeWordFromNextResultSet(resultSet: ResultSet): Word? {
        if (!resultSet.next()) {
            return null
        }

        val id = resultSet.getInt("id")
        val word = resultSet.getString("word")

        return Word(id, word)
    }
}