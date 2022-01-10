package com.kafka.replier.config;

import com.kafka.replier.exception.CircularReferenceException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kafka.replier.config.StubProperties.Consumer;
import static com.kafka.replier.config.StubProperties.Producer;
import static com.kafka.replier.config.StubProperties.StubEntry;
import static com.kafka.replier.fixture.StubPropertyFixture.stubProperties;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StubPropertiesTest {

    @Test
    void thatCheckCircularReferencesThrowsNoExceptionWhenThereIsNoCircularDefinition() {
        StubProperties stubProperties = stubProperties();

        assertThatCode(stubProperties::checkCircularReferences).doesNotThrowAnyException();
    }

    @Test
    void thatCheckCircularReferencesThrowsExceptionWhenThereIsACircularDefinition() {
        StubProperties properties = new StubProperties();
        properties.setStubs(List.of(stubEntry("consumer", "producer"), stubEntry("producer", "consumer")));

        assertThatThrownBy(properties::checkCircularReferences)
                .isInstanceOf(CircularReferenceException.class);
    }

    private StubEntry stubEntry(String consumerTopic, String producerTopic) {
        StubEntry entry = new StubEntry();
        Consumer consumer = new Consumer();
        consumer.setJsonPathFilter("jsonPathFilter");
        consumer.setTopic(consumerTopic);
        entry.setConsumer(consumer);
        Producer producer = new Producer();
        producer.setTopic(producerTopic);
        producer.setData("data");
        producer.setReplyTemplate("replyTemplate");
        producer.setReplyFile("replyFile");
        entry.setProducer(producer);
        return entry;
    }
}