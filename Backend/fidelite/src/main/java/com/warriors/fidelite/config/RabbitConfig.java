package com.warriors.fidelite.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "evenements";
    public static final String QUEUE = "fidelite.queue";

    @Bean
    TopicExchange evenementsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue fideliteQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Declarables fideliteBindings(Queue fideliteQueue, TopicExchange evenementsExchange) {
        Binding inscription = BindingBuilder.bind(fideliteQueue).to(evenementsExchange).with("inscription.confirmee");
        Binding presence = BindingBuilder.bind(fideliteQueue).to(evenementsExchange).with("presence.enregistree");
        Binding avis = BindingBuilder.bind(fideliteQueue).to(evenementsExchange).with("avis.envoye");
        return new Declarables(inscription, presence, avis);
    }

    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
