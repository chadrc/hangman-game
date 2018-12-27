package com.chadrc.hangman

import kotlinx.css.*

fun CSSBuilder.mainStyles() {
    body {
        backgroundColor = Color.red
    }
    p {
        fontSize = 2.em
    }
    rule("p.myclass") {
        color = Color.blue
    }
}
