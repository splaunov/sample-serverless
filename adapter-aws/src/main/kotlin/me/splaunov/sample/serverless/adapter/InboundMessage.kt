package me.splaunov.sample.serverless.adapter

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * An inbound message with all the data of a new user registration event
 */
data class InboundMessage(
    val name: String,

    val id: Long,

    /**
     * Date and time when the user was registered in ISO 8601 format
     */
    @JsonProperty("created_at")
    var created: LocalDateTime
)
