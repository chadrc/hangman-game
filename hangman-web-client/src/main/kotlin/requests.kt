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
        console.log("fetch response", it)
        when {
            it.ok -> it.json().then { data -> {
                console.log("json response", data)
                callback(data as T)
            } }
            else -> it.text().then { message -> console.error("Failed request $message") }
        }
    }
}

fun startGameRequest(callback: (GameResponse) -> Unit) {
    console.log("start game request")
    State.startingGameProp.value = true
    makeRequest("/start", "POST", callback)
    State.startingGameProp.value = false
}