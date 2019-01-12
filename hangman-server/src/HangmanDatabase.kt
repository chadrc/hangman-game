package com.chadrc.hangman

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import models.Game
import models.GameResult
import models.Guess
import models.Word
import java.lang.Exception
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

fun makeConnectionPool(): HikariDataSource {
    val host = AppConfig.property("hangman.database.host")
    val port = AppConfig.property("hangman.database.port")
    val databaseName = AppConfig.property("hangman.database.databaseName")
    val username = AppConfig.property("hangman.database.username")
    val password = AppConfig.property("hangman.database.password")
    val connectionPoolSize = AppConfig.property("hangman.database.connectionPoolSize")

    val props = Properties()
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.user", username)
    props.setProperty("dataSource.password", password)
    props.setProperty("dataSource.databaseName", databaseName)
    props.setProperty("dataSource.portNumber", port)
    props.setProperty("dataSource.serverName", host)

    val config = HikariConfig(props)
    config.maximumPoolSize = connectionPoolSize?.toInt() ?: 10
    return try {
        HikariDataSource(config)
    } catch (_: Exception) {
        val h2Config = HikariConfig()
        h2Config.jdbcUrl = "jdbc:h2:mem:"
        h2Config.driverClassName = "org.h2.Driver"
        config.maximumPoolSize = 1

        HikariDataSource(h2Config)
    }
}

class HangmanDatabase {
    private val datasource: HikariDataSource = makeConnectionPool()

    private fun <T> withConnection(func: (conn: Connection) -> T): T {
        val conn = datasource.connection
        val result = func(conn)
        conn.close()
        return result
    }

    fun close() {
        datasource.close()
    }

    fun createWord(word: String): Word = withConnection {
        val statement = it.prepareStatement("""
            INSERT INTO words (word)
            VALUES (?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)

        statement.setString(1, word)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys

        resultSet.next()

        val id = resultSet.getInt("id")

        resultSet.close()
        statement.close()

        Word(id, word)
    }

    fun getWordById(id: Int): Word? = withConnection {
        val statement = it.prepareStatement("""
            SELECT * FROM words WHERE id=?
        """.trimIndent())

        statement.setInt(1, id)

        executeAndGetFirstWord(statement)
    }

    fun createGame(wordId: Int, guessesAllowed: Int): Game = withConnection {
        val statement = it.prepareStatement("""
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

        Game(id, wordId, guessesAllowed)
    }

    fun getGame(gameId: Int): Game? = withConnection {
        val statement = it.prepareStatement("""
            SELECT * FROM games WHERE id=?
        """.trimIndent())

        statement.setInt(1, gameId)

        val resultSet = statement.executeQuery()

        if (!resultSet.next()) {
            return@withConnection null
        }

        val id = resultSet.getInt("id")
        val wordId = resultSet.getInt("word_id")
        val guessesAllowed = resultSet.getInt("guesses_allowed")

        resultSet.close()
        statement.close()

        Game(id, wordId, guessesAllowed)
    }

    fun getWord(word: String): Word? = withConnection {
        val statement = it.prepareStatement("""
            SELECT * FROM words WHERE word=?
        """.trimIndent())

        statement.setString(1, word)

        executeAndGetFirstWord(statement)
    }

    fun createGuess(gameId: Int, guess: Char): Guess = withConnection {
        val statement = it.prepareStatement("""
            INSERT INTO character_guesses (game_id, guess)
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

        Guess(id, gameId, guess.toString())
    }

    fun createWordGuess(gameId: Int, word: String): Guess = withConnection {
        val statement = it.prepareStatement("""
            INSERT INTO word_guesses (game_id, guess)
            VALUES(?, ?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)

        statement.setInt(1, gameId)
        statement.setString(2, word)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys

        resultSet.next()

        val id = resultSet.getInt("id")

        resultSet.close()
        statement.close()

        Guess(id, gameId, word)
    }

    fun getRandomWord(): Word? = withConnection {
        val statement = it.prepareStatement("""
            SELECT * FROM words OFFSET floor(random()*(SELECT COUNT(*) FROM words)) LIMIT 1
        """.trimIndent())

        executeAndGetFirstWord(statement)
    }

    fun getGuessesWithGameId(gameId: Int): List<Guess> = withConnection {
        val statement = it.prepareStatement("""
            SELECT * FROM character_guesses WHERE game_id=?
        """.trimIndent())

        statement.setInt(1, gameId)

        val resultSet = statement.executeQuery()

        val guesses = mutableListOf<Guess>()

        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val guessStr = resultSet.getString("guess")

            guesses.add(Guess(id, gameId, guessStr))
        }

        resultSet.close()
        statement.close()

        guesses
    }

    fun getWordGuessesByGameId(gameId: Int): List<Guess> = withConnection {
        val statement = it.prepareStatement("""
            SELECT * FROM word_guesses WHERE game_id=?
        """.trimIndent())

        statement.setInt(1, gameId)

        val resultSet = statement.executeQuery()

        val guesses = mutableListOf<Guess>()

        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val guessStr = resultSet.getString("guess")

            guesses.add(Guess(id, gameId, guessStr))
        }

        resultSet.close()
        statement.close()

        guesses
    }

    fun createWonGameResult(gameId: Int, won: Boolean): GameResult = withConnection {
        val statement = it.prepareStatement("""
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

        GameResult(id, gameId, won)
    }

    fun createForfeitGameResult(gameId: Int, forfeit: Boolean): GameResult = withConnection {
        val statement = it.prepareStatement("""
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

        GameResult(id, gameId, forfeit = forfeit)
    }

    fun getGameResultWithGameId(gameId: Int): GameResult? = withConnection {
        val statement = it.prepareStatement("""
            SELECT * FROM game_results WHERE game_id=?
        """.trimIndent())

        statement.setInt(1, gameId)

        val resultSet = statement.executeQuery()

        if (!resultSet.next()) {
            return@withConnection null
        }

        val id = resultSet.getInt("id")
        val wonObj = resultSet.getObject("won")
        val won = if (wonObj is Boolean) wonObj else null
        val forfeitObj = resultSet.getObject("forfeit")
        val forfeit = if (forfeitObj is Boolean) forfeitObj else null

        resultSet.close()
        statement.close()

        GameResult(id, gameId, won, forfeit)
    }

    private fun executeAndGetFirstWord(statement: PreparedStatement): Word? = withConnection {
        val resultSet = statement.executeQuery()

        val wordObj = makeWordFromNextResultSet(resultSet)

        resultSet.close()
        statement.close()

        wordObj
    }

    private fun makeWordFromNextResultSet(resultSet: ResultSet): Word? = withConnection {
        if (!resultSet.next()) {
            return@withConnection null
        }

        val id = resultSet.getInt("id")
        val word = resultSet.getString("word")

        Word(id, word)
    }
}