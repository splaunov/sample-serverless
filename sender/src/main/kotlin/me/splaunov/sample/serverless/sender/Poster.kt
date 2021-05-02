package me.splaunov.sample.serverless.sender

import kotlinx.coroutines.reactive.awaitSingle
import me.splaunov.sample.serverless.common.debug
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

/**
 * Service for sending message to external REST API
 */
@Service
class Poster(private val config: ConfigurationProperties) {
    private val logger = LoggerFactory.getLogger(Poster::class.java)

    suspend fun post(outMsg: OutboundMessage) {
        logger.debug { "Sending message: $outMsg" }

        WebClient.create()
            .post()
            .uri(config.pushServiceUri)
            .body(Mono.just(outMsg), OutboundMessage::class.java)
            .retrieve()
            .toBodilessEntity()
            .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
            .awaitSingle()

        logger.debug { "Sent to endpoint: ${config.pushServiceUri}" }
    }
}

