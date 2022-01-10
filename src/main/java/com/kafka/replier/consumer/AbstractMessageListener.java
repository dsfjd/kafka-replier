package com.kafka.replier.consumer;

import com.kafka.replier.service.MessageForwardingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.UUID;

import static com.kafka.replier.config.StubProperties.Consumer;
import static com.kafka.replier.config.StubProperties.Producer;

/**
 * Abstrakter Kafka Listener
 */
public abstract class AbstractMessageListener {

    private static int NUMBER_OF_LISTENERS = 0;

    public abstract KafkaListenerEndpoint createKafkaListenerEndpoint(String name, Consumer consumer, Producer producer,
                                                                      MessageForwardingService messageForwardingService)
            throws NoSuchMethodException;

    protected MethodKafkaListenerEndpoint<String, String> createDefaultMethodKafkaListenerEndpoint(String name, String topic) {
        String uniqueGroupId = UUID.randomUUID().toString();
        MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint =
                new MethodKafkaListenerEndpoint<>();
        kafkaListenerEndpoint.setId(getConsumerId(name));
        kafkaListenerEndpoint.setGroupId(uniqueGroupId);
        kafkaListenerEndpoint.setAutoStartup(true);
        kafkaListenerEndpoint.setTopics(topic);
        kafkaListenerEndpoint.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());
        return kafkaListenerEndpoint;
    }

    private String getConsumerId(String name) {
        if (StringUtils.isBlank(name)) {
            return AbstractMessageListener.class.getCanonicalName() + "#" + NUMBER_OF_LISTENERS++;
        } else {
            return name;
        }
    }
}