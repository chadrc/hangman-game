package com.chadrc.hangman

import kotlinx.html.*

fun HEAD.scriptAsset(name: String) {
    script {
        src = "/assets/js/$name.js"
    }
}

fun HEAD.cssLink(uri: String) {
    link {
        href = uri
        rel = "stylesheet"
        type = "text/css"
    }
}

fun HTML.indexPage() {
    head {
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
        }

        cssLink("https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.2/css/bulma.css")
        cssLink("/styles.css")

        script {
            defer = true
            src = "https://use.fontawesome.com/releases/v5.3.1/js/all.js"
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
