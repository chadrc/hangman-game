import kotlinx.html.button
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.header
import kotlinx.html.js.onClickFunction
import kotlinx.html.main
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.addEventListener("load", {

        val body = document.body!!

        body.append {
            header {
                h1 {
                    +"Hangman"
                }
            }
            main {
                button {
                    onClickFunction = { event ->
                        console.log("click", event)

                        console.log("coroutine started")

                        val response = startGameRequest { response ->
                            console.log(response)
                        }

                        console.log("response", response)
                    }

                    +"Start Game"
                }
            }
        }
    })
}