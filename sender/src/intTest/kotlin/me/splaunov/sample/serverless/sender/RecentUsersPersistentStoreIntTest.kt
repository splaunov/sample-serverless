package me.splaunov.sample.serverless.sender

import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSingleElement
import me.splaunov.sample.serverless.AppConfig
import me.splaunov.sample.serverless.adapter.store.LocalStackStoreEndpointSetter
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import java.time.Instant
import kotlin.test.Test

@SpringBootTest(classes = [LocalStackStoreEndpointSetter::class, AppConfig::class])
internal class RecentUsersPersistentStoreIntTest {

    @Autowired
    private lateinit var store: RecentUsersPersistentStore

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Autowired
    private lateinit var config: ConfigurationProperties

    private val resource: Resource by lazy {
        resourceLoader.getResource(config.recentUsersListResourceURL)
    }

    @BeforeEach
    internal fun setUp() {
        if (resource.exists()) {
            val wr = resource as WritableResource
            wr.outputStream.use { stream -> stream.write(byteArrayOf()) }
        }
    }

    @Test
    fun `save and load`() {
        val user = User("user1", 0, Instant.now())

        store.save(setOf(user))
        val actualList = store.load()

        actualList.shouldHaveSingleElement(user)
        resource.exists().shouldBeTrue()
        val json = resource.inputStream.bufferedReader().use { it.readText() }
        json.shouldContainJsonKeyValue("$.[0].name", user.name)
        json.shouldContainJsonKeyValue("$.[0].id", user.id)
    }

}