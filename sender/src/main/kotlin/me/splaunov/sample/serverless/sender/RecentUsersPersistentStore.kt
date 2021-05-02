package me.splaunov.sample.serverless.sender

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import org.springframework.stereotype.Service

/**
 * Simple abstraction to save and load list of users to/from persistent store.
 *
 * @property config Configuration parameters.
 * @property resourceLoader Loader which is responsible for communication with
 * the persistent store.
 * @property objectMapper Mapper for JSON conversion operations.
 */
@Service
class RecentUsersPersistentStore(
    private val config: ConfigurationProperties,
    private val resourceLoader: ResourceLoader,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(RecentUsersPersistentStore::class.java)

    private val resource = resourceLoader.getResource(config.recentUsersListResourceURL)

    /**
     * Loads current list of users from the persistent store.
     *
     * @return Retrieved list.
     */
    fun load(): Set<User> {
        var result = setOf<User>()
        try {
            result = resource.inputStream.use { stream -> objectMapper.readValue(stream) }
            logger.debug("List file was loaded from the store.")
        } catch (e: Exception) {
            if (resource.exists()) throw e
            logger.debug("List file was not found in the store.")
        }
        return result
    }

    /**
     * Save list of users to persistent store.
     *
     * @param recentUsers List to save.
     */
    fun save(recentUsers: Set<User>) {
        val writableResource = resource as WritableResource
        writableResource.outputStream.use { stream -> objectMapper.writeValue(stream, recentUsers) }
        logger.debug("List file was uploaded to the store.")
    }
}