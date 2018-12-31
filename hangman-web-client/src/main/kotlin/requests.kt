import org.w3c.fetch.*
import responses.GameResponse
import kotlin.browser.window

fun <T> makeRequest(path: String, method: String, callback: (T) -> Unit) {
    val request = Request(
        path, RequestInit(
            method = method,
            cache = RequestCache.NO_CACHE,
            redirect = RequestRedirect.ERROR
        )
    )

    window.fetch(request).then {
        when (it.status.toInt()) {
            200 -> it.json()
                .then { data -> callback(data as T) }
                .catch { throwable -> console.error("JSON error", throwable) }
            else -> it.text().then { message -> console.error("Failed request $message") }
        }
    }
}

fun <T> intercept(func: (T) -> Unit, before: () -> Unit): (T) -> Unit = {
    before()
    func(it)
}

fun startGameRequest(callback: (GameResponse) -> Unit) {
    State.startingGameProp.value = true
    makeRequest("/start", "POST", intercept(callback) {
        State.startingGameProp.value = false
    })
}