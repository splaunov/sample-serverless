package me.splaunov.sample.serverless.sender

import io.kotest.matchers.shouldBe
import java.time.Instant
import kotlin.test.Test

class TemplatesEngineTest {

    private val templatesEngine = TemplatesEngine()

    @Test
    fun `with 3 recent users`() {

        val actualMessage = templatesEngine.composeWelcomeMessage(
            user = "Marcus".toUser(),
            recentUsers = setOf("Lise".toUser(), "Anna".toUser(), "Stephen".toUser())
        )

        actualMessage shouldBe "Hi Marcus, welcome. Lise, Anna and Stephen also joined recently."
    }

    @Test
    fun `with no recent users`() {

        val actualMessage = templatesEngine.composeWelcomeMessage(
            user = "Marcus".toUser(),
            recentUsers = setOf()
        )

        actualMessage shouldBe "Hi Marcus, welcome."
    }

    private fun String.toUser() =
        User(this, 0, Instant.parse("2021-01-01T00:00:00Z"))
}