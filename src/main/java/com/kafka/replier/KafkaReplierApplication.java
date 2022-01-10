package com.kafka.replier;

import com.kafka.replier.config.StubProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Diese Anwendung hört auf Kafka Topics und produziert auf Basis der {@link StubProperties} Kafka Events.
 */
@EnableKafka
@SpringBootApplication
@ConfigurationPropertiesScan
class KafkaReplierApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaReplierApplication.class, args);
    }
}
