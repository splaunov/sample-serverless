package me.splaunov.sample.serverless

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
open class AppConfig

fun main(args: Array<String>) {
    runApplication<AppConfig>(*args)
}