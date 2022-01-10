package com.kafka.replier.fixture;

import com.kafka.replier.config.StubProperties;
import lombok.experimental.UtilityClass;

import java.util.List;

import static com.kafka.replier.config.StubProperties.Consumer;
import static com.kafka.replier.config.StubProperties.Producer;
import static com.kafka.replier.config.StubProperties.StubEntry;

@UtilityClass
public class StubPropertyFixture {

    public static StubProperties stubProperties() {
        StubProperties properties = new StubProperties();
        StubEntry entry = new StubEntry();
        Consumer consumer = new Consumer();
        consumer.setJsonPathFilter("jsonPathFilter");
        consumer.setTopic("input");
        entry.setConsumer(consumer);
        Producer producer = new Producer();
        producer.setTopic("output");
        producer.setData("data");
        producer.setReplyTemplate("replyTemplate");
        producer.setReplyFile("replyFile");
        entry.setProducer(producer);
        properties.setStubs(List.of(entry));
        return properties;
    }
}
