package com.kafka.replier.consumer;

import com.kafka.replier.config.StubProperties;
import com.kafka.replier.config.StubProperties.StubEntry;
import com.kafka.replier.exception.KafkaSetupListenerException;
import com.kafka.replier.service.MessageForwardingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

/**
 * Diese Klasse initialisiert auf Basis der {@link StubProperties} die Kafka-Listener, welche Replies weiterleiten
 */
@Slf4j
@Component
public class CustomKafkaListenerRegistrar implements InitializingBean {

    private static final String MESSAGE_FORWARDER_LISTENER_CLASS = "com.kafka.replier.consumer.MessageForwarderListener";
    //todo unit test me
    @Autowired
    StubProperties properties;

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    KafkaListenerContainerFactory kafkaListenerContainerFactory;

    @Autowired
    MessageForwardingService messageForwardingService;

    /**
     * Initialisiert die Kafka Listener auf basis der {@link StubProperties}, welche über ein JSON bereitgestellt werden
     */
    @Override
    public void afterPropertiesSet() {
        properties.checkCircularReferences();
        properties.getStubs().forEach(this::registerCustomKafkaListener);
    }

    private void registerCustomKafkaListener(StubEntry stubEntry) {
        try {
            AbstractMessageListener messageForwardListener =
                    (AbstractMessageListener) beanFactory.getBean(Class.forName(MESSAGE_FORWARDER_LISTENER_CLASS));
            KafkaListenerEndpoint kafkaListenerEndpoint =
                    messageForwardListener.createKafkaListenerEndpoint(randomUUID().toString(), stubEntry.getConsumer(),
                                                                       stubEntry.getProducer(), messageForwardingService);
            kafkaListenerEndpointRegistry.registerListenerContainer(kafkaListenerEndpoint, kafkaListenerContainerFactory, true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            log.error("Could not create Kafka Listener for StubEntry:{}", stubEntry, e);
            throw new KafkaSetupListenerException(e);
        }
    }
}