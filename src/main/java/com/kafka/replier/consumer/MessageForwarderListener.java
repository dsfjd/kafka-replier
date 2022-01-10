package com.kafka.replier.consumer;

import com.kafka.replier.config.StubProperties.Consumer;
import com.kafka.replier.config.StubProperties.Producer;
import com.kafka.replier.config.StubProperties.StubEntry;
import com.kafka.replier.service.MessageForwardingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Message Forward Listener
 */
@Component
@AllArgsConstructor
public class MessageForwarderListener extends AbstractMessageListener {
    //todo unit test me

    /**
     * Erstellt ein Kafka Message Forward Listener Endpunkt für ein {@link StubEntry}
     *
     * @param name                     des Listeners
     * @param consumer                 consumer Config
     * @param producer                 producer Config
     * @param messageForwardingService forwarding Service
     * @return den Kafka Listener
     * @throws NoSuchMethodException, wenn die onMessage Methode nicht gefunden werden kann.
     */
    @Override
    public KafkaListenerEndpoint createKafkaListenerEndpoint(String name, Consumer consumer, Producer producer,
                                                             MessageForwardingService messageForwardingService)
            throws NoSuchMethodException {
        MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint =
                createDefaultMethodKafkaListenerEndpoint(name, consumer.getTopic());
        kafkaListenerEndpoint.setBean(new MessageForwarder(consumer, producer, messageForwardingService));
        kafkaListenerEndpoint.setMethod(MessageForwarder.class.getMethod("onMessage", ConsumerRecord.class));
        return kafkaListenerEndpoint;
    }

    /**
     * Der Message Listener der die Nachricht weiterleitet
     */
    @Slf4j
    @AllArgsConstructor
    public static class MessageForwarder implements MessageListener<String, String> {

        Consumer consumer;
        Producer producer;
        MessageForwardingService messageForwardingService;

        /**
         * Leitet die Kafka Nachricht an den {@link MessageForwardingService} weiter
         *
         * @param record Kafka consumer Message
         */
        @Override
        public void onMessage(ConsumerRecord<String, String> record) {
            messageForwardingService.handleEvent(record, consumer, producer);
        }
    }
}

