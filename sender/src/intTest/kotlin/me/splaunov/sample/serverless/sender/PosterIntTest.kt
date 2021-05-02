package me.splaunov.sample.serverless.sender

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test

class PosterIntTest {

    private val server = MockWebServer()
    private val config = mockk<ConfigurationProperties>()

    @BeforeAll
    fun setUp() {
        server.enqueue(MockResponse())
        server.start()
        every { config.pushServiceUri } returns server.url("").toString()
    }

    @AfterAll
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun post() {
        runBlocking {
            Poster(config).post(
                OutboundMessage(
                    sender = "some@mail",
                    receiver = 111,
                    message = "Hi Marcus, ...",
                    recent_user_ids = setOf(222, 333, 444)
                )
            )

            val request = server.takeRequest()
            request.body.readUtf8() shouldEqualJson
                    """{"sender":"some@mail",
                |"receiver":111,
                |"message":"Hi Marcus, ...",
                |"recent_user_ids":[222,333,444]}"""
                        .trimMargin()
            request.getHeader("Content-Type") shouldBe "application/json"
        }
    }
}