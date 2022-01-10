package com.kafka.replier.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service für das Senden von Kafka Topics
 */
@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaSendingService {

    //todo unit test me
    KafkaTemplate<String, String> template;

    /**
     * Leitet die Reply auf das angegebene topic weiter
     *
     * @param topic auf das die Reply geschickt werden soll
     * @param reply die Nachricht die auf das Topic geschickt werden soll
     */
    public void replyToTopic(String topic, String reply) {
        template.send(topic, reply)
                .addCallback(success -> log.info("Sent reply to topic:{}", topic),
                             failure -> log.error("Error occurred during sending reply to topic:{}", topic));
    }
}
