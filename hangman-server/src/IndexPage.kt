package com.chadrc.hangman

import kotlinx.html.*

fun HEAD.scriptAsset(name: String) {
    script {
        src = "/assets/js/$name.js"
    }
}

fun HTML.indexPage() {
    head {
        link {
            href = "/styles.css"
            rel = "stylesheet"
            type = "text/css"
        }

        scriptAsset("kotlin")
        scriptAsset("hangman-common")
        scriptAsset("main")
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
