package com.kafka.replier;

import com.kafka.replier.config.StubProperties;
import com.kafka.replier.config.StubProperties.StubEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static com.kafka.replier.config.StubProperties.Consumer;
import static com.kafka.replier.config.StubProperties.Producer;
import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka
@DirtiesContext
@SpringBootTest(properties = { "stub-file-location=classpath:stub-examples/example.json" })
class JsonToPropertyIT {

    @Autowired
    StubProperties properties;

    @Test
    void shouldMapPropertiesCorrectly() {
        StubEntry e1 = new StubEntry();
        StubProperties.Consumer consumer = new StubProperties.Consumer();
        consumer.setTopic("a");
        e1.setConsumer(consumer);
        Producer producer = new Producer();
        producer.setTopic("b");
        e1.setProducer(producer);
        StubEntry e2 = new StubEntry();
        StubProperties.Consumer consumer2 = new Consumer();
        consumer2.setTopic("c");
        e2.setConsumer(consumer2);
        Producer producer2 = new Producer();
        producer2.setTopic("d");
        e2.setProducer(producer2);

        assertThat(properties.getStubs()).isNotNull().containsExactlyInAnyOrder(e1, e2);
    }
}
