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

    /** Participation a un evenement : +50 points. */
    public static final String KEY_PARTICIPATION = "presence.enregistree";
    /** Avis depose : +20 points. */
    public static final String KEY_AVIS = "avis.envoye";
    /** Victoire a un hackathon (service Competitions) : +300 points. */
    public static final String KEY_VICTOIRE = "competition.gagnee";

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
        Binding participation = BindingBuilder.bind(fideliteQueue).to(evenementsExchange).with(KEY_PARTICIPATION);
        Binding avis = BindingBuilder.bind(fideliteQueue).to(evenementsExchange).with(KEY_AVIS);
        Binding victoire = BindingBuilder.bind(fideliteQueue).to(evenementsExchange).with(KEY_VICTOIRE);
        return new Declarables(participation, avis, victoire);
    }

    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
