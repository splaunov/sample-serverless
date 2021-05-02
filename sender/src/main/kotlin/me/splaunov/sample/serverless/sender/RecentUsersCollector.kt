package me.splaunov.sample.serverless.sender

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

/**
 * Collects recently registered users in a persistent list.
 */
@Service
class RecentUsersCollector(
    private val config: ConfigurationProperties,
    private val persistentStore: RecentUsersPersistentStore,
    private val clock: Clock
) {
    private val logger = LoggerFactory.getLogger(RecentUsersCollector::class.java)

    /**
     * Oldest [User.created] date of all users loaded from the store
     */
    private val oldestLoadedEvent: Instant?
        get() = recentUsers.minByOrNull { it.created }?.created
    private var recentUsers = setOf<User>()

    /**
     * Ads new user registration event into the persistent list. Or discards the event if it is older
     * than [ConfigurationProperties.dontCollectEventsOlderThanHours].
     * Deletes all events older than [ConfigurationProperties.dontCollectEventsOlderThanHours] from the list.
     *
     * @param newUser New user registration event.
     */
    fun collect(newUser: User) {
        logger.debug("Collecting new registered user.")
        loadList()
        if (newUser.isCreatedEarlierThan(config.dontCollectEventsOlderThanHours) && isLoadedListStale()) {
            /* This might happen that between the call to [isLoadedListStale] and [getListTruncatedTo]
            * other thread have loaded a fresh list. This leads to one extra save to the store.
            * Not a big deal, no need to synchronize threads.*/
            val usersToSave = getListTruncatedTo(config.maxRecentUsersInWelcomeMessage) as MutableSet<User>
            usersToSave.removeIf { !it.isCreatedEarlierThan(config.dontCollectEventsOlderThanHours) }
            usersToSave.add(newUser)
            persistentStore.save(usersToSave)
            recentUsers = usersToSave
        }
    }

    /**
     * Returns list of recent users. Loads the list from the store if update needed.
     *
     * @param newUser New user, who should be excluded from the returned list
     * @return List of recent users
     */
    fun getRecentUsers(newUser: User): Set<User> {
        logger.debug("getRecentUsers was called.")
        loadList()
        return getListTruncatedTo(config.maxRecentUsersInWelcomeMessage, newUser)
    }

    private fun getListTruncatedTo(maxLength: Int, userToExclude: User? = null): Set<User> =
        recentUsers
            .filterNot { it.id == userToExclude?.id }
            .sortedByDescending { it.created }
            .distinctBy { it.name }
            .take(maxLength)
            .toMutableSet()

    private fun loadList() {
        if (isLoadedListStale()) recentUsers = persistentStore.load()
    }

    private fun isLoadedListStale(): Boolean {
        val oldest = oldestLoadedEvent
        return if (oldest == null) true
        else oldest.until(
            OffsetDateTime.ofInstant(clock.instant(), clock.zone),
            ChronoUnit.SECONDS
        ) > config.recentUsersListStaleThresholdSeconds
    }

    private fun User.isCreatedEarlierThan(hours: Int): Boolean =
        created.until(clock.instant(), ChronoUnit.HOURS) < hours
}