package me.splaunov.sample.serverless.adapter.aws

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import me.splaunov.sample.serverless.adapter.InboundMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.function.adapter.aws.FunctionInvoker
import org.springframework.context.annotation.Bean
import org.springframework.messaging.converter.MessageConverter
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.function.Function
import kotlin.test.Test

internal class SqsMessageConverterIntTest {

    @Test
    fun `process SQS event`() {
        System.setProperty("MAIN_CLASS", SQSTestConfiguration::class.java.name)
        System.setProperty("spring.cloud.function.definition", "processTestSQSEvent")
        val output = ByteArrayOutputStream()

        FunctionInvoker().handleRequest(
            javaClass.getResourceAsStream("/sqs-message.json"),
            output,
            null
        )

        val actualResult = String(output.toByteArray(), StandardCharsets.UTF_8)
        actualResult shouldBe "\"Anna+Paul\"" //FunctionInvoker wraps output string to quotes.
    }

    @EnableAutoConfiguration
    open class SQSTestConfiguration {
        @Bean
        open fun processTestSQSEvent(): Function<List<InboundMessage>, String> {
            return Function { messages -> messages[0].name + "+" + messages[1].name  }
        }

        @Bean
        open fun messageConverter(@Autowired mapper: ObjectMapper): MessageConverter {
            return SqsMessageConverter(mapper)
        }
    }

}
