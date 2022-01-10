package com.kafka.replier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka
@SpringBootTest
class KafkaReplierApplicationIT {

    @Test
    void contextLoads() {
    }
}
