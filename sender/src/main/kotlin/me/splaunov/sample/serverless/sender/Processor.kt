package me.splaunov.sample.serverless.sender

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.splaunov.sample.serverless.adapter.InboundMessage
import org.springframework.stereotype.Service
import java.time.ZoneId

/**
 * Top-level class of the sender service implementation.
 * Orchestrate calls to all other detailed services.
 */
@Service
class Processor(
    private val config: ConfigurationProperties,
    private val recentUsersCollector: RecentUsersCollector,
    private val templatesEngine: TemplatesEngine,
    private val poster: Poster,
) {
    suspend fun process(messages: List<InboundMessage>) {
        coroutineScope {
            for (message in messages) {
                val newUser = User(
                    message.name,
                    message.id,
                    message.created.atZone(ZoneId.of(config.inboundMessageTimeZone)).toInstant()
                )

                val recentUsers = recentUsersCollector.getRecentUsers(newUser)
                recentUsersCollector.collect(newUser)

                launch {
                    poster.post(
                        OutboundMessage(
                            sender = config.senderEMail,
                            receiver = newUser.id,
                            message = templatesEngine.composeWelcomeMessage(newUser, recentUsers),
                            recent_user_ids = recentUsers.map { user -> user.id }.toSet()
                        )
                    )
                }
            }
        }
    }
}