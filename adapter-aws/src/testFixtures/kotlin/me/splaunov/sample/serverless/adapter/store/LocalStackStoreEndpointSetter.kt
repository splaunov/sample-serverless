package me.splaunov.sample.serverless.adapter.store

import me.splaunov.sample.serverless.adapter.LocalStackContainerManager
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.testcontainers.containers.localstack.LocalStackContainer.Service.S3

/**
 * This bean factory post processor overrides endpoint value for Amazon S3 client.
 * Add it to your test's Spring context like this:
 * ```
 * @SpringBootTest(classes = [LocalStackStoreExtension::class, AppConfig::class])
 * ```
 * Also the LocalStack container is started if it was not started before.
 */
class LocalStackStoreEndpointSetter : BeanFactoryPostProcessor {
//    override fun afterAll(extensionContext: ExtensionContext) {
//        localstack.stop()
//    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        beanFactory.getBeanDefinition("amazonS3").propertyValues.add(
            "customEndpoint",
            LocalStackContainerManager.localstack.getEndpointOverride(S3).toString()
        )
    }
}