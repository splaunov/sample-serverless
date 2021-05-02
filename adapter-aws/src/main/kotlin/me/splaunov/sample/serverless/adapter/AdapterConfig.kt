package me.splaunov.sample.serverless.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import me.splaunov.sample.serverless.adapter.aws.SqsMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MessageConverter

@Configuration
open class AdapterConfig {

    @Bean
    open fun messageConverter(@Autowired mapper: ObjectMapper): MessageConverter {
        return SqsMessageConverter(mapper)
    }
}