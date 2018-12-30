package com.chadrc.hangman

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import responses.GameResponse

val hangmanService = HangmanService()

fun Routing.hangmanRestRoutes() {
    post("/start") {
        val startGameResult = hangmanService.startGame()
        if (startGameResult is Ok) {
            val gameInfo = startGameResult.result()
            call.respond(GameResponse(gameInfo.game, gameInfo.guesses, gameInfo.result, null))
        } else if (startGameResult is Error) {
            call.respond(HttpStatusCode.InternalServerError, startGameResult.message)
        }
    }

    get("/game") {
        call.respond(mapOf("message" to "Fetching Game"))
    }

    post("/guess") {
        call.respond(mapOf("message" to "Making Guess"))
    }

    post("/forfeit") {
        call.respond(mapOf("message" to "Forfeiting Game"))
    }
}