import org.w3c.fetch.*
import responses.GameResponse
import kotlin.browser.window

fun <T> makeRequest(path: String, method: String, callback: (T) -> Unit) {
    val request = Request(path, RequestInit(
        method = method,
        cache = RequestCache.NO_CACHE,
        redirect = RequestRedirect.ERROR
    ))

    window.fetch(request).then {
        when {
            it.ok -> it.json().then { data -> callback(data as T) }
            else -> it.text().then { message -> console.error("Failed request $message") }
        }
    }
}

fun startGameRequest(callback: (GameResponse) -> Unit) = makeRequest("/start", "POST", callback)