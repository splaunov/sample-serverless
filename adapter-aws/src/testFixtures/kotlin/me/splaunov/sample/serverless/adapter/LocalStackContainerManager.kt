package me.splaunov.sample.serverless.adapter

import me.splaunov.sample.serverless.adapter.LocalStackContainerManager.localstack
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

/**
 * Starts LocalStackContainer in a lazy way - on the first read of the [localstack] property.
 * Container is stopped on JVM shutdown.
 */
object LocalStackContainerManager {
    val localstack: LocalStackContainer by lazy {

        val container = LocalStackContainer(DockerImageName.parse("localstack/localstack:0.12.9"))
            .withServices(LocalStackContainer.Service.S3)
        container.start()
        container.execInContainer("awslocal", "s3", "mb", "s3://sample-serverless")

        Runtime.getRuntime().addShutdownHook(Thread {
            container.stop()
        })

        return@lazy container
    }
}