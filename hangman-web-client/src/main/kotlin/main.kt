import kotlin.browser.document
import kotlin.browser.window
import kotlinx.html.*
import kotlinx.html.dom.*
import kotlinx.html.js.onClickFunction

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
                    onClickFunction = {
                        console.log("Starting Game")
                    }

                    + "Start Game"
                }
            }
        }
    })
}