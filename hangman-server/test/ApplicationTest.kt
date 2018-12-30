package com.chadrc.hangman

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.content.*
import io.ktor.http.content.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.jackson.*
import io.ktor.features.*
import kotlin.test.*
import io.ktor.server.testing.*
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
