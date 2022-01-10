package com.kafka.replier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.ONE_SECOND;
import static org.awaitility.Durations.TEN_SECONDS;

@EmbeddedKafka
@DirtiesContext
@SpringBootTest(properties = { "stub-file-location=classpath:stub-examples/stub-template.json" })
class TemplateReplyIT {

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

    @Test
    @SneakyThrows
    void shouldForwardTheGivenReplyFile() {
        String payload = readResource("input/complex.json");
        String replacedTemplate = readResource("replies/expected-replaced-template.json");
        kafkaTemplate.send("input", payload).get();

        await().atMost(TEN_SECONDS)
                .untilAsserted(() ->
                               {
                                   ConsumerRecords<String, String> poll = consumer.poll(ONE_SECOND);
                                   assertThat(poll)
                                           .extracting(ConsumerRecord::topic, ConsumerRecord::value)
                                           .containsExactly(tuple("output", replacedTemplate));
                               }
                );
    }

    private String readResource(String path) {
        try {
            Resource resource = new DefaultResourceLoader().getResource(path);
            Reader reader = new InputStreamReader(resource.getInputStream());
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(path, e);
        }
    }
}

