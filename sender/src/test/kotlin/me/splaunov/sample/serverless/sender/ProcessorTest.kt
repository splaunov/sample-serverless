package me.splaunov.sample.serverless.sender

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import me.splaunov.sample.serverless.adapter.InboundMessage
import java.time.Instant
import java.time.LocalDateTime
import kotlin.test.Test

class ProcessorTest {

    private val config = mockk<ConfigurationProperties>().apply {
        every { senderEMail } returns "sender@email.domain"
        every { inboundMessageTimeZone } returns "UTC"
    }
    private val templatesEngine = mockk<TemplatesEngine>()
    private val collector = mockk<RecentUsersCollector>(relaxed = true)

    @Test
    fun `check outbound message assembly`() {
        val poster = mockk<Poster>(relaxed = true)
        val processor = Processor(
            config,
            mockCollectorWith(1, 2),
            mockTemplatesEngineWith("SomeMessage"),
            poster
        )

        runBlocking {
            processor.process(
                listOf(
                    InboundMessage("newUserName1", 98, LocalDateTime.now()),
                    InboundMessage("newUserName2", 99, LocalDateTime.now()),
                )
            )
        }

        verify {
            runBlocking {
                poster.post(
                    OutboundMessage(
                        "sender@email.domain",
                        98,
                        "SomeMessage",
                        setOf(1, 2)
                    )
                )
                poster.post(
                    OutboundMessage(
                        "sender@email.domain",
                        99,
                        "SomeMessage",
                        setOf(1, 2)
                    )
                )
            }
        }

        confirmVerified(poster)
    }


    private fun mockTemplatesEngineWith(@Suppress("SameParameterValue") message: String) =
        templatesEngine.apply {
            every { composeWelcomeMessage(any(), any()) } returns message
        }


    private fun mockCollectorWith(vararg usersIds: Int) =
        collector.apply {
            every { getRecentUsers(any()) } returns
                    usersIds.map {
                        User("User$it", it.toLong(), Instant.now())
                    }.toSet()
        }

}