package me.splaunov.sample.serverless.adapter.aws

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.splaunov.sample.serverless.adapter.InboundMessage
import org.springframework.messaging.Message
import org.springframework.messaging.converter.AbstractMessageConverter
import java.lang.reflect.ParameterizedType

/**
 * Converts an incoming AWS SQS message to a list of [InboundMessage].
 */
class SqsMessageConverter(private val objectMapper: ObjectMapper) : AbstractMessageConverter() {
    override fun convertFromInternal(
        message: Message<*>,
        targetClass: Class<*>,
        conversionHint: Any?
    ): List<InboundMessage>? {
        with(conversionHint) {
            if (this !is ParameterizedType
                || rawType != List::class.java
                || actualTypeArguments.size != 1
                || actualTypeArguments[0] != InboundMessage::class.java
            ) return null
        }

        logger.debug("Converting SQSEvent to User")

        val serializerSQSEvent: PojoSerializer<SQSEvent> =
            LambdaEventSerializers.serializerFor(SQSEvent::class.java, javaClass.classLoader)

        val sqsEvent = serializerSQSEvent.fromJson(
            when (message.payload) {
                is String -> message.payload as String
                is ByteArray -> String(message.payload as ByteArray)
                else -> throw IllegalArgumentException("Not supported inbound message type: ${message.payload::class}")
            }
        )
        return sqsEvent.records.map { objectMapper.readValue(it.body) }

    }

    override fun supports(clazz: Class<*>): Boolean =
        true // Can't check type here because of type erasure. Will do it in [convertFromInternal].
}