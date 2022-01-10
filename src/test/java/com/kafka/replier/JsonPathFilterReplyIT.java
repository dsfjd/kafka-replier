package com.kafka.replier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Durations.ONE_SECOND;
import static org.awaitility.Durations.TEN_SECONDS;

@EmbeddedKafka
@DirtiesContext
@Slf4j
@SpringBootTest(properties = { "stub-file-location=classpath:stub-examples/stub-jsonpath.json" })
class JsonPathFilterReplyIT {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, String> consumer;

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("group", "true", embeddedKafkaBroker);
        consumer = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer()).createConsumer();
        consumer.subscribe(singleton("output"));
    }

    @SneakyThrows
    @Test
    void shouldForwardTheConfiguredMessageWhenJsonPathMatches() {
        kafkaTemplate.send("input", "{\"test\":\"data\"}").get();

        await().atMost(TEN_SECONDS)
                .untilAsserted(() ->
                                       assertThat(consumer.poll(ONE_SECOND))
                                               .extracting(ConsumerRecord::topic, ConsumerRecord::value)
                                               .containsExactly(tuple("output", "{\"some\":\"message\"}"))
                );
    }

    @SneakyThrows
    @Test
    void shouldNotForwardTheConfiguredMessageWhenJsonDoesNotPathMatches() {
        kafkaTemplate.send("input", "{\"test\":\"invalid\"}").get();

        with()
                .atMost(ofSeconds(20))
                .pollDelay(ofSeconds(10))
                .untilAsserted(() -> assertThat(consumer.poll(ONE_SECOND)).isEmpty());
    }
}

