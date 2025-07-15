package com.mulato.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String CRAWL_QUEUE = "crawl.queue";
    public static final String CRAWL_EXCHANGE = "crawl.exchange";
    public static final String CRAWL_ROUTING_KEY = "crawl.task";

    @Bean
    public Queue crawlQueue() {
        return new Queue(CRAWL_QUEUE, true);
    }

    @Bean
    public TopicExchange crawlExchange() {
        return new TopicExchange(CRAWL_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue crawlQueue, TopicExchange crawlExchange) {
        return BindingBuilder.bind(crawlQueue).to(crawlExchange).with(CRAWL_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
