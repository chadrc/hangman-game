import org.w3c.fetch.*
import requests.ForfeitRequest
import requests.GuessRequest
import responses.GameResponse
import kotlin.browser.window
import kotlin.js.Promise

fun <T> makeRequest(path: String, method: String, body: Any? = null): Promise<T> {
    val request = Request(
        path, RequestInit(
            method = method,
            cache = RequestCache.NO_CACHE,
            redirect = RequestRedirect.ERROR,
            body = JSON.stringify(body)
        )
    )

    return Promise { resolve, reject ->
        window.fetch(request).then {
            when (it.status.toInt()) {
                200 -> it.json()
                    .then { data -> resolve(data as T) }
                    .catch { throwable -> reject(Error("JSON error", throwable)) }
                else -> it.text().then { message -> reject(Error("Failed request $message")) }
            }
        }
    }
}

fun <T> makePostRequest(path: String, body: Any? = null): Promise<T> =
    makeRequest(path, "POST", body)

fun <T> makeGetRequest(path: String): Promise<T> =
    makeRequest(path, "GET")

fun makeStartGameRequest(): Promise<GameResponse> =
    makePostRequest("/start")

fun makeGetGameRequest(id: Int): Promise<GameResponse> =
    makeGetRequest("/game/$id")

fun makeGuessRequest(gameId: Int, guess: String): Promise<GameResponse> =
    makePostRequest("/guess", GuessRequest(gameId, guess))

fun makeForfeitRequest(gameId: Int): Promise<GameResponse> =
    makePostRequest("/forfeit", ForfeitRequest(gameId))