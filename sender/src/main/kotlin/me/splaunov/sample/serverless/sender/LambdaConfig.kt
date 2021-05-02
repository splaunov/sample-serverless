package me.splaunov.sample.serverless.sender

import kotlinx.coroutines.runBlocking
import me.splaunov.sample.serverless.adapter.InboundMessage
import me.splaunov.sample.serverless.common.debug
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.util.function.Consumer

@Configuration
open class LambdaConfig {
    private val logger = LoggerFactory.getLogger(LambdaConfig::class.java)

    /**
     * Source of instant for temporal calculations
     */
    @Bean
    open fun clock(): Clock {
        return Clock.systemDefaultZone()
    }

    /**
     * Entry point of serverless function
     *
     */
    //TODO Change method signature to return Kotlin lambda after this issue is fixed:
    // https://github.com/spring-cloud/spring-cloud-function/issues/509
    @Bean
    open fun process(processor: Processor): Consumer<List<InboundMessage>> {
        return Consumer {
            logger.debug { "Processor input: $it" }
            runBlocking { processor.process(it) }
        }
    }
}

