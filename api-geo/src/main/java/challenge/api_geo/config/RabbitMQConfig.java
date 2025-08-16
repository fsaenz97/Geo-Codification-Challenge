package challenge.api_geo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REQUESTS_QUEUE  = "geocoding_requests_queue";
    public static final String RESPONSES_QUEUE = "geocoding_responses_queue";

    @Bean
    public Queue geocodingRequestsQueue() {
        return new Queue(REQUESTS_QUEUE, true);
    }

    @Bean
    public Queue geocodingResponsesQueue() {
        return new Queue(RESPONSES_QUEUE, true);
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter mc) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(mc);
        return template;
    }
}
