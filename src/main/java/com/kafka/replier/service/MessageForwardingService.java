package com.kafka.replier.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static com.kafka.replier.config.StubProperties.Consumer;
import static com.kafka.replier.config.StubProperties.Producer;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Service für das weiter leiten von Replies
 */
@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageForwardingService {

    //todo unit test me
    KafkaSendingService sendingService;
    TemplateService templateService;
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Value("${reply-file-folder:replies}")
    String replyLocation;

    /**
     * @param consumerRecord das empfangene Kafka Event
     * @param consumer       enthält das JsonPath Query für das filtert von Requests
     * @param producer       enthält die Informationen wohin und wie die Reply weitergeleitet werden sol
     */
    public void handleEvent(ConsumerRecord<String, String> consumerRecord, Consumer consumer, Producer producer) {
        log.info("received record on topic:{}", consumerRecord.topic());
        String jsonPathFilter = consumer.getJsonPathFilter();
        String producerData = producer.getData();
        String producerReplyTemplate = producer.getReplyTemplate();
        if (nonNull(jsonPathFilter)) {
            DocumentContext context = JsonPath.parse(consumerRecord.value());
            List matchedData = context.read(jsonPathFilter, List.class);
            if (matchedData.isEmpty()) {
                return;
            }
        }
        if (nonNull(producerReplyTemplate) && isNull(producerData)) {
            producerData = templateService.replaceTemplate(consumerRecord.value(), producerReplyTemplate);
        }
        if (isNull(producerData)) {
            producerData = readResource(producer.getReplyFile());
        }
        sendingService.replyToTopic(producer.getTopic(), producerData);
    }

    private String readResource(String fileName) {
        try {
            Resource resource = resourceLoader.getResource(replyLocation + "/" + fileName);
            Reader reader = new InputStreamReader(resource.getInputStream());
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(fileName, e);
        }
    }
}
