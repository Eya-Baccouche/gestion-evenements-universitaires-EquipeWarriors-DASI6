package com.warriors.feedback.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "feedback.exchange";
    public static final String QUEUE_AVIS = "avis.depose.queue";
    public static final String ROUTING_AVIS = "avis.depose";

    @Bean
    public TopicExchange feedbackExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue avisDeposeQueue() {
        return QueueBuilder.durable(QUEUE_AVIS).build();
    }

    @Bean
    public Binding avisBinding(Queue avisDeposeQueue, TopicExchange feedbackExchange) {
        return BindingBuilder.bind(avisDeposeQueue).to(feedbackExchange).with(ROUTING_AVIS);
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
