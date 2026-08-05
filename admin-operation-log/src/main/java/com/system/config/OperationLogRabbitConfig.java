package com.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.common.log.OperationLogConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 操作日志消息队列配置。
 */
@Configuration
public class OperationLogRabbitConfig {

    @Bean
    public DirectExchange operationLogExchange() {
        return new DirectExchange(OperationLogConstants.EXCHANGE, true, false);
    }

    @Bean
    public Queue operationLogQueue() {
        return QueueBuilder.durable(OperationLogConstants.QUEUE).build();
    }

    @Bean
    public Binding operationLogBinding(Queue operationLogQueue, DirectExchange operationLogExchange) {
        return BindingBuilder.bind(operationLogQueue)
                .to(operationLogExchange)
                .with(OperationLogConstants.ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
