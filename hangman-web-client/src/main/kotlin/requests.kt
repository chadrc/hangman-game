import org.w3c.fetch.*
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