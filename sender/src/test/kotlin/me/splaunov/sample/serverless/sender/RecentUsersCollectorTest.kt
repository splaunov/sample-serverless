package me.splaunov.sample.serverless.sender

import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class RecentUsersCollectorTest {
    private val now = Instant.parse("2021-03-01T00:00:00Z")

    private val config = mockk<ConfigurationProperties>(relaxed = true).apply {
        every { dontCollectEventsOlderThanHours } returns 24
        every { maxRecentUsersInWelcomeMessage } returns 2
        every { recentUsersListStaleThresholdSeconds } returns 1
    }
    private val persistentStore: RecentUsersPersistentStore = mockk()

    @ParameterizedTest(name = "{0}")
    @MethodSource
    fun getRecentUsers(
        @Suppress("UNUSED_PARAMETER") caseDescription: String,
        storedList: Set<User>, newUser: User, expectedList: Set<User>
    ) {
        every { persistentStore.load() } returns storedList

        val actualList = newCollector().getRecentUsers(newUser)

        actualList shouldBe expectedList
    }

    @Suppress("unused")
    private fun getRecentUsers() = listOf<Arguments>(
        Arguments.of(
            "empty list in the store",
            setOf<User>(),
            newUser(),
            setOf<User>()
        ),
        Arguments.of(
            "truncate if list is longer than needed",
            setOf(
                registeredUser(4),
                registeredUser(2),
                registeredUser(0),
                registeredUser(1),
                registeredUser(3),
            ),
            newUser(),
            setOf(
                registeredUser(0),
                registeredUser(1),
            )
        ),
        Arguments.of(
            "exclude current user if present in list",
            setOf(
                registeredUser(0),
                newUser()
            ),
            newUser(),
            setOf(
                registeredUser(0),
            )
        ),
        Arguments.of(
            "exclude duplicate names",
            setOf(
                registeredUser(1, "Same Name"),
                registeredUser(0, "Same Name"),
            ),
            newUser(),
            setOf(
                registeredUser(0, "Same Name"),
            )
        ),
    )

    @ParameterizedTest(name = "{0}")
    @MethodSource
    fun collect(
        @Suppress("UNUSED_PARAMETER") caseDescription: String,
        storedList: Set<User>, newUser: User, expectedList: Set<User>
    ) {
        var actualList: Set<User> = setOf()
        every { persistentStore.load() } returns storedList
        every { persistentStore.save(any()) } answers { actualList = firstArg() }

        newCollector().collect(newUser)

        actualList shouldBe expectedList
    }

    @Suppress("unused")
    private fun collect() = listOf<Arguments>(
        Arguments.of(
            "collect first event",
            setOf<User>(),
            newUser(),
            setOf(newUser())
        ),
        Arguments.of(
            "collect old event",
            setOf<User>(),
            registeredUser(config.dontCollectEventsOlderThanHours + 1),
            setOf<User>()
        ),
        Arguments.of(
            "collect new event when old events are in the list",
            setOf(
                registeredUser(config.dontCollectEventsOlderThanHours + 1),
                registeredUser(config.dontCollectEventsOlderThanHours + 10),
                registeredUser(config.dontCollectEventsOlderThanHours - 5),
            ),
            newUser(),
            setOf(
                registeredUser(config.dontCollectEventsOlderThanHours - 5),
                newUser(),
            )
        ),
    )

    @Nested
    inner class OptimizeStoreCommunications {
        @Test
        fun `no calls to store if cached in Collector list is not stale`() {
            val collector = newCollector()
            every { persistentStore.load() } returns setOf(
                registeredUser(0)
            )
            collector.getRecentUsers(registeredUser(0)) //load cache
            clearMocks(persistentStore)

            collector.collect(newUser())

            confirmVerified(persistentStore) //no calls to the store
        }

        @Test
        fun `calls store if cached in Collector list is stale`() {
            val collector = newCollector()
            val staleUser = User("newUser1", 1, now.minusSeconds(2))
            every { persistentStore.load() } returns setOf(staleUser)
            collector.getRecentUsers(registeredUser(0)) //load cache
            clearMocks(persistentStore)
            every { persistentStore.load() } returns setOf(staleUser)
            justRun { persistentStore.save(any()) }

            collector.collect(newUser())

            verify {
                persistentStore.load()
                persistentStore.save(setOf(staleUser, newUser()))
            }
            confirmVerified(persistentStore)
        }
    }

    private fun newUser(name: String = "newUser") = User(name, 999, now)

    private fun registeredUser(hoursEarlier: Int, name: String = "$hoursEarlier days earlier") = User(
        name,
        hoursEarlier.toLong(),
        now.minus(Duration.ofHours(hoursEarlier.toLong()))
    )

    private fun newCollector() = RecentUsersCollector(
        config,
        persistentStore,
        Clock.fixed(now, ZoneId.systemDefault())
    )

}