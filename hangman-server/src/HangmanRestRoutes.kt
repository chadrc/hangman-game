package com.chadrc.hangman

import com.chadrc.hangman.errors.GameNotFoundError
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import models.GameInfo
import requests.ForfeitRequest
import requests.GuessRequest
import responses.GameResponse

val hangmanService = HangmanService()

fun GameInfo.isComplete() = result?.won != null || result?.forfeit != null

suspend inline fun <reified T : Any> ApplicationCall.tryReceive(): T? =
    try { receiveOrNull() } catch (exception: Exception) { null }

fun Routing.hangmanRestRoutes() {
    post("/start") {
        val startGameResult = hangmanService.startGame()

        if (startGameResult is Ok) {
            val gameInfo = startGameResult.result()
            val word = if (gameInfo.isComplete()) gameInfo.word else null
            call.respond(GameResponse(gameInfo.game, word, gameInfo.guesses, gameInfo.result))
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
                val word = if (gameInfo.isComplete()) gameInfo.word else null
                call.respond(GameResponse(gameInfo.game, word, gameInfo.guesses, gameInfo.result))
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
                val newGameInfo = makeGuessResult.result()
                val word = if (newGameInfo.isComplete()) newGameInfo.word else null
                call.respond(GameResponse(newGameInfo.game, word, newGameInfo.guesses, newGameInfo.result))
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
                call.respond(GameResponse(gameInfo.game, gameInfo.word, gameInfo.guesses, gameInfo.result))
            }
            is GameNotFoundError -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(HttpStatusCode.InternalServerError, (forfeitResult as Error).message)
        }
    }
}