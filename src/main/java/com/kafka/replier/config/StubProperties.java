package com.kafka.replier.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kafka.replier.exception.CircularReferenceException;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * In dieser Klasse wird die Stub Konfiguration bereitgestellt. Dieses wird über ein Json eingelesen.
 */
@Data
@Slf4j
@Component
@ConfigurationProperties
@PropertySource(value = "${stub-file-location:classpath:stub.json}", factory = JsonPropertySourceFactory.class)
public class StubProperties {

    @JsonProperty("stubs")
    List<StubEntry> stubs = new ArrayList<>();

    /**
     * Definiert das Format von einem Topic auf das gehört werden soll mit der entsprechenden Reply Aktion auf das definierte.
     */
    @Data
    @FieldDefaults(level = PRIVATE)
    public static class StubEntry {

        @JsonProperty("consumer")
        Consumer consumer;
        @JsonProperty("producer")
        Producer producer;
    }

    /**
     * Enthält die Informationen auf welches Kafka Topic gehört werden soll.
     * <p>
     * Beispiel Json Konfiguration
     * <code>
     * "consumer": {
     * "jsonPathFilter": "$[?(@.test=='data')]",
     * "topic": "topic-name"
     * }</code>
     */

    @Data
    @FieldDefaults(level = PRIVATE)
    public static class Consumer {

        /**
         * Dieses Feld ein valides JsonPath Statement angegeben werden, wenn das Statement einen Treffer findet,
         * wird der Eintrag gemäß {@link StubProperties.Producer} Konfiguration weitergeleitet, wenn das Feld nicht definiert wird,
         * wird nicht gefiltert.
         */
        @Nullable
        @JsonProperty("jsonPathFilter")
        String jsonPathFilter;
        /**
         * Beschreibt das Topic auf das gehört werden sollt
         */
        @JsonProperty("topic")
        String topic;
    }

    /**
     * Enthält die Informationen wo hin welche Reply gesendet werden soll
     * <p>
     * Beispiel Konfiguration:
     * <code>
     * "producer": {
     * "topic": "new",
     * "data": "{\"producer\":\"automatic producer\"}"
     * }
     * </code>
     */
    @Data
    @FieldDefaults(level = PRIVATE)
    public static class Producer {

        /**
         * Beschreibt den Namen des Topics, auf den die Reply geschickt werden soll.
         */
        @JsonProperty("topic")
        String topic;
        /**
         * Dieses Feld wird entweder über die Json Konfiguration mit einer direkten Reply befüllt,
         * alternativ kann aber auf das Feld "replyFile" mit dem Dateinamen befüllt werden.
         * Wenn jedoch das Feld {@link #replyTemplate} befüllt ist, wird dieses bevorzugt benutzt.
         */
        @JsonProperty("data")
        String data;
        /**
         * Dieses Feld wird benutzt, um den Dateinamen des Reply-Templates zu definieren
         */
        @JsonProperty("replyTemplate")
        String replyTemplate;
        /**
         * Dieses Feld wird benutzt, um den Dateinamen eines Reply-Files zu definieren
         */
        @JsonProperty("replyFile")
        String replyFile;
    }

    public void checkCircularReferences() {
        stubs.forEach(stubEntry -> {
            String consumerTopic = stubEntry.getConsumer().getTopic();
            stubs.forEach(e -> {
                String producerTopic = e.getProducer().getTopic();
                if (consumerTopic.equals(producerTopic)) {
                    log.error("Could not setup the kafka replier because the json config contains a circular reference on topic:{}",
                              consumerTopic);
                    throw new CircularReferenceException(stubEntry, e);
                }
            });
        });
    }
}
