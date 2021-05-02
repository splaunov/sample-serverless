package me.splaunov.sample.serverless.sender

import java.time.Instant

/**
 * Information about registered user.
 * Is created from [me.splaunov.sample.serverless.adapter.InboundMessage] by
 * converting [created] date to [Instant].
 */
@Suppress("EqualsOrHashCode")
data class User(
    val name: String,

    val id: Long,

    /**
     * Date and time when the user was registered
     */
    var created: Instant
)