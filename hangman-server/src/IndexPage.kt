package com.chadrc.hangman

import kotlinx.html.*

fun HTML.indexPage() {
    head {
        link {
            href = "/styles.css"
            rel = "stylesheet"
            type = "text/css"
        }
    }

    body {
        h1 { +"HTML" }
        ul {
            for (n in 1..10) {
                li { +"$n" }
            }
        }
    }
}
