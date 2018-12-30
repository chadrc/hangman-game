package com.chadrc.hangman

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

fun Routing.hangmanRestRoutes() {
    post("/start") {
        call.respond(mapOf("message" to "Starting Game"))
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