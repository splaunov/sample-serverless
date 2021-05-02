package me.splaunov.sample.serverless.sender

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "sample-serverless")
data class ConfigurationProperties(
    /**
     * If incoming event is older than this number of hours,
     * then it will not be collected into the recent users list
     */
    val dontCollectEventsOlderThanHours: Int,
    /**
     * Maximum number of recent users in a welcome message
     */
    val maxRecentUsersInWelcomeMessage: Int,
    val recentUsersListResourceURL: String,
    /**
     * Email address of the notification service
     */
    val senderEMail: String,
    val pushServiceUri: String,
    /**
     * Inbound SNS sends created_at date with local, unqualified zone.
     * So this property is used for proper conversion to [java.time.OffsetDateTime].
     */
    val inboundMessageTimeZone: String,
    val recentUsersListStaleThresholdSeconds: Int,
) {
}