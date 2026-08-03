package com.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 操作日志 RabbitMQ 配置。
 *
 * DirectExchange -> 声明交换机
 * Queue          -> 声明队列
 * Binding        -> 绑定交换机和队列
 * MessageConverter -> 消息 JSON 序列化/反序列化
 */
@EnableRabbit
@Configuration
public class OperationLogRabbitConfig {

    public static final String OPERATION_LOG_EXCHANGE = "admin.operation.log.exchange";
    public static final String OPERATION_LOG_QUEUE = "admin.operation.log.queue";
    public static final String OPERATION_LOG_ROUTING_KEY = "admin.operation.log";

    @Bean
    public DirectExchange operationLogExchange() {
        return new DirectExchange(OPERATION_LOG_EXCHANGE, true, false);
    }

    @Bean
    public Queue operationLogQueue() {
        return QueueBuilder.durable(OPERATION_LOG_QUEUE).build();
    }

    @Bean
    public Binding operationLogBinding(Queue operationLogQueue, DirectExchange operationLogExchange) {
        return BindingBuilder.bind(operationLogQueue).to(operationLogExchange).with(OPERATION_LOG_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
