import kotlinx.html.button
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.header
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.p
import kotlinx.html.js.span
import kotlinx.html.main
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    getGame()
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
                    disableIfGettingGame()
                    hideIfInvalidGameId()

                    onClickFunction = { startGame() }

                    +"Start Game"
                }

                div {
                    showIfValidGameId()

                    p {
                        updateWithWord { word ->
                            for (c in word) {
                                span("hangman-character") {
                                    + c.toString()
                                }
                            }
                        }
                    }
                }
            }
        }

        Binder.bindElements()
    })
}