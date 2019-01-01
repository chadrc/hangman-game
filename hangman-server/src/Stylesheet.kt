package com.chadrc.hangman

import kotlinx.css.*
import kotlinx.css.properties.borderBottom

fun CSSBuilder.mainStyles() {
    rule(".hangman-character") {
        borderBottom(2.px, BorderStyle.solid, Color.black)
    }
}
