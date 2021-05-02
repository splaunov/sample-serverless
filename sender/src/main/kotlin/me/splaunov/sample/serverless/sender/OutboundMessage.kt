package me.splaunov.sample.serverless.sender

/**
 * Message to be sent to the external push-notifications service.
 */
data class OutboundMessage(
    val sender: String,
    val receiver: Long,
    val message: String,
    val recent_user_ids: Set<Long>,
)