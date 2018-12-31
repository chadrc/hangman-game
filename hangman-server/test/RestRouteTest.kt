package com.chadrc.hangman

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import responses.GameResponse
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.TestInstance
import requests.ForfeitRequest
import requests.GuessRequest
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@KtorExperimentalAPI
class RestRouteTest {
    private val utils = TestUtils()
    private val hangmanDatabase = HangmanDatabase()
    private val hangmanService = HangmanService(hangmanDatabase)
    private val mapper = jacksonObjectMapper()

    @Before
    fun setUp() {
        utils.basicDataSetup()
    }

    @After
    fun cleanUp() {
        utils.emptyAll()
        hangmanDatabase.close()
        utils.close()
    }

    private fun <R> withHangmanTestApplication(test: TestApplicationEngine.() -> R): R {
        return withTestApplication({ module(true, hangmanService) }, test)
    }

    @Test
    fun startGame() {
        withHangmanTestApplication {
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

        withHangmanTestApplication {
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

    @Test
    fun `Get game that does not exist returns 404`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Get, "/game/1").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `Get game with invalid id returns 400`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Get, "/game/notanid").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `Make guess on started game`() {
        val game = (hangmanService.startGame() as Ok).result().game

        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/guess") {
                setBody(mapper.writeValueAsString(
                    GuessRequest(game.id, "c")
                ))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val content = response.content ?: fail("No content in response")
                val data: GameResponse = mapper.readValue(content)

                assertNotNull(data.game)
                assertNotNull(data.guesses)
                assertEquals(1, data.guesses.size)
                assertNull(data.result)
                assertNull(data.word)
            }
        }
    }

    @Test
    fun `Make guess on game that does not exist returns not found`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/guess") {
                setBody(mapper.writeValueAsString(
                    GuessRequest(10, "c")
                ))
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `Make guess with empty guess returns bad request`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/guess") {
                setBody(mapper.writeValueAsString(
                    GuessRequest(10, "")
                ))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `Make guess with missing gameId returns bad request`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/guess") {
                setBody(mapper.writeValueAsString(
                    mapOf(
                        "guess" to "c"
                    )
                ))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `Make guess with missing guess returns bad request`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/guess") {
                setBody(mapper.writeValueAsString(
                    mapOf(
                        "gameId" to 1
                    )
                ))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `Make guess with no parameters returns bad request`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/guess") {
                setBody(mapper.writeValueAsString("{}"))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `Make forfeit request on started game`() {
        val game = (hangmanService.startGame() as Ok).result().game

        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/forfeit") {
                setBody(mapper.writeValueAsString(ForfeitRequest(game.id)))
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())

            val content = response.content ?: fail("No content in response")
            val data: GameResponse = mapper.readValue(content)

            assertNotNull(data.game)
            assertNotNull(data.guesses)
            assertNotNull(data.result)
            assertNotNull(data.word)
        }
    }

    @Test
    fun `Make forfeit request on game that does not exist returns not found`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/forfeit") {
                setBody(mapper.writeValueAsString(ForfeitRequest(1)))
            }
        }.apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun `Make forfeit request with no gameId returns bad request`() {
        withHangmanTestApplication {
            handleRequest(HttpMethod.Post, "/forfeit") {
                setBody(mapper.writeValueAsString("{}"))
            }
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }
}