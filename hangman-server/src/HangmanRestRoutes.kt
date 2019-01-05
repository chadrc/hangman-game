package com.chadrc.hangman

import com.chadrc.hangman.errors.GameNotFoundError
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import models.GameInfo
import models.GameResult
import models.Guess
import requests.ForfeitRequest
import requests.GuessRequest
import responses.GameResponse
import responses.GameResultResponse

fun GameInfo.isComplete() = result?.won != null || result?.forfeit != null

suspend inline fun <reified T : Any> ApplicationCall.tryReceive(): T? =
    try { receiveOrNull() } catch (exception: Exception) { null }

fun resultToResponse(result: GameResult?): GameResultResponse? {
    if (result == null) {
        return null
    }

    return GameResultResponse(result.won, result.forfeit)
}

fun guessesToResponse(guesses: List<Guess>): List<String> = guesses.map { it.guess }

fun spaceUnfoundLetters(word: String, guesses: List<Guess>): String {
    val characterGuesses = guesses.filter { it.guess.length == 1 }.map { it.guess[0] }
    val spacedWordBuilder = StringBuilder()

    for (c in word.toCharArray()) {
        val newC = if (characterGuesses.contains(c)) {
            c
        } else {
            ' '
        }

        spacedWordBuilder.append(newC)
    }

    return spacedWordBuilder.toString()
}

fun gameToResponse(gameInfo: GameInfo): GameResponse {
    val word = if (gameInfo.isComplete()) {
        gameInfo.word
    } else {
        spaceUnfoundLetters(gameInfo.word, gameInfo.guesses)
    }
    return GameResponse(
        gameInfo.game.id,
        word,
        guessesToResponse(gameInfo.guesses),
        resultToResponse(gameInfo.result)
    )
}

fun Routing.hangmanRestRoutes(hangmanService: HangmanService) {
    post("/start") {
        val startGameResult = hangmanService.startGame()

        if (startGameResult is Ok) {
            val gameInfo = startGameResult.result()
            call.respond(gameToResponse(gameInfo))
        } else if (startGameResult is Error) {
            call.respond(HttpStatusCode.InternalServerError, startGameResult.message)
        }
    }

    get("/game/{gameId}") {
        val gameIdStr = call.parameters["gameId"]

        val gameId = gameIdStr?.toIntOrNull()
        if (gameId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val getGameResult = hangmanService.getGame(gameId)

        when (getGameResult) {
            is Ok -> {
                val gameInfo = getGameResult.result()
                call.respond(gameToResponse(gameInfo))
            }
            is GameNotFoundError -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(HttpStatusCode.InternalServerError, (getGameResult as Error).message)
        }
    }

    post("/guess") {
        val data = call.tryReceive<GuessRequest>()

        if (data == null || data.gameId == -1) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val makeGuessResult = when {
            data.guess.length == 1 -> hangmanService.makeGuess(data.gameId, data.guess[0])
            data.guess.isNotEmpty() -> hangmanService.makeWordGuess(data.gameId, data.guess)
            else -> null
        }

        if (makeGuessResult == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        when (makeGuessResult) {
            is Ok -> {
                val gameInfo = makeGuessResult.result()
                call.respond(gameToResponse(gameInfo))
            }
            is GameNotFoundError -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(HttpStatusCode.InternalServerError, (makeGuessResult as Error).message)
        }
    }

    post("/forfeit") {
        val data = call.tryReceive<ForfeitRequest>()

        if (data == null || data.gameId == -1) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val forfeitResult = hangmanService.forfeitGame(data.gameId)

        when (forfeitResult) {
            is Ok -> {
                val gameInfo = forfeitResult.result()
                call.respond(gameToResponse(gameInfo))
            }
            is GameNotFoundError -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(HttpStatusCode.InternalServerError, (forfeitResult as Error).message)
        }
    }
}