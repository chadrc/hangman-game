package com.chadrc.hangman

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.css.*

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondHtml {
                indexPage()
            }
        }

        get("/styles.css") {
            call.respondCss {
                mainStyles()
            }
        }

        static("/static") {
            resources("static")
        }

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
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
