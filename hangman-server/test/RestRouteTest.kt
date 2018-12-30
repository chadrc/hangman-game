package com.chadrc.hangman

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import org.junit.Test
import responses.GameResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

@KtorExperimentalAPI
class RestRouteTest {
    private val utils = TestUtils()
    @Test
    fun startGame() {
        utils.basicDataSetup()
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/start").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val mapper = jacksonObjectMapper()
                val content = response.content ?: fail("No content in response")
                val data: GameResponse = mapper.readValue(content)

                assertNotNull(data.game)
                assertNotNull(data.guesses)
                assertNull(data.result)
                assertNull(data.word)
            }
        }
    }
}