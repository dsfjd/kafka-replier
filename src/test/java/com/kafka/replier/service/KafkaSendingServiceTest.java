package com.kafka.replier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class KafkaSendingServiceTest {

    KafkaSendingService kafkaSendingService;
    @Mock
    KafkaTemplate<String, String> template;

    @BeforeEach
    void setUp() {
        kafkaSendingService = new KafkaSendingService(template);
    }

    @Test
    void replyToTopic() {
    //todo write unit tests
    }
}