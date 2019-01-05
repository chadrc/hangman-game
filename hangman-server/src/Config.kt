package com.chadrc.hangman

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory

fun Config.tryGetString(path: String): String? {
    return try {
        getString(path)
    } catch (_: ConfigException.Missing) {
        null
    }
}

object AppConfig {
    private val baseConfig = ConfigFactory.load("hangman")
    private var envConfig: Config? = null

    init  {
        val env: String? = System.getenv("HANGMAN_ENV")
        if (env != null) {
            envConfig = ConfigFactory.load("hangman-$env")
        }
    }

    fun property(path: String): String? {
        return envConfig?.tryGetString(path) ?: baseConfig.tryGetString(path)
    }
}