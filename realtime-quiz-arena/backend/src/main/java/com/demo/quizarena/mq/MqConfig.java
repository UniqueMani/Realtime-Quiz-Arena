package com.demo.quizarena.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demo MQ scaffolding. Extend with:
 * - exchange + routing keys
 * - consumers for scoring/analytics
 */
@Configuration
public class MqConfig {
    public static final String ANSWER_SUBMITTED_QUEUE = "answer.submitted";

    @Bean
    public Queue answerSubmittedQueue() {
        return new Queue(ANSWER_SUBMITTED_QUEUE, true);
    }
}
