package me.splaunov.sample.serverless.sender

import org.springframework.stereotype.Service

/**
 * Service for composing an outbound welcome message.
 */
@Service
class TemplatesEngine {

    /**
     * Composes an outbound welcome message.
     *
     * @param user Inbound message about new registered user
     * @param recentUsers List of recently registered users
     * @return Outbound welcome message
     */
    fun composeWelcomeMessage(user: User, recentUsers: Set<User>): String =
        StringBuilder()
            .append("Hi ${user.name}, welcome.")
            .apply {
                if (recentUsers.isNotEmpty()) {
                    append(" ")
                        .appendJoinedNames(recentUsers)
                        .append(" also joined recently.")
                }
            }.toString()
}

private fun StringBuilder.appendJoinedNames(users: Set<User>): StringBuilder {
    users.forEachIndexed { i, user ->
        this.apply {
            when (i) {
                in 1 until users.size - 1 -> append(", ")
                users.size - 1 -> append(" and ")
            }
        }.append(user.name)
    }
    return this
}
