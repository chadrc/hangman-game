package com.chadrc.hangman

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest
import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import java.util.*

fun Config.tryGetString(path: String): String? {
    return try {
        getString(path)
    } catch (_: ConfigException.Missing) {
        null
    }
}

fun makeSSMConfig(basePath: String): Config? {
    return try {
        val ssm = AWSSimpleSystemsManagementClientBuilder.defaultClient()

        val getParametersResponse = ssm.getParametersByPath(GetParametersByPathRequest().apply {
            path = basePath
            recursive = true
        })

        val properties = Properties()
        for (parameter in getParametersResponse.parameters) {
            // convert ssm path to a properties path
            val name = parameter.name
                .trim('/')
                .replace('/', '.')

            properties[name] = parameter.value
        }

        ConfigFactory.parseProperties(properties)
    } catch (exception: Exception) {
        println("Error creating SSM config: ${exception.message}")
        null
    }
}

object AppConfig {
    private val baseConfig = ConfigFactory.load("hangman")
    private var envConfig: Config? = null
    private var ssmConfig: Config? = null

    init  {
        val env: String? = System.getenv("HANGMAN_ENV")
        var ssmPath = "/hangman"

        if (env != null) {
            envConfig = ConfigFactory.load("hangman-$env")
            ssmPath = "$ssmPath/$env"
            envConfig
        }

        ssmPath += "/"

        ssmConfig = makeSSMConfig(ssmPath)
    }

    fun property(path: String): String? {
        return ssmConfig?.tryGetString(path)
            ?: envConfig?.tryGetString(path)
            ?: baseConfig.tryGetString(path)
    }
}