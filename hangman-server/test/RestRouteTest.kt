package com.chadrc.hangman

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import responses.GameResponse
import org.junit.After
import org.junit.Before
import kotlin.test.*

@KtorExperimentalAPI
class RestRouteTest {
    private val utils = TestUtils()
    private val hangmanService = HangmanService()
    private val mapper = jacksonObjectMapper()

    @Before
    fun setUp() {
        utils.basicDataSetup()
    }

    @After
    fun cleanUp() {
        utils.emptyAll()
    }

    @Test
    fun startGame() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/start").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val content = response.content ?: fail("No content in response")
                val data: GameResponse = mapper.readValue(content)

                assertNotNull(data.game)
                assertNotNull(data.guesses)
                assertNull(data.result)
                assertNull(data.word)
            }
        }
    }

    @Test
    fun `Get started game`() {
        val game = (hangmanService.startGame() as Ok).result().game

        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/game/${game.id}").apply {
                assertEquals(HttpStatusCode.OK, response.status())

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