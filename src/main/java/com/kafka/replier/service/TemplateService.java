package com.kafka.replier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.kafka.replier.exception.TemplateException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Service für das Ersetzen von templates
 */
@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TemplateService {

    //todo unit test me
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Value("${reply-file-folder:replies}")
    String replyLocation;

    /**
     * Ersetzt das replyTemplate mit dem consumerJson
     *
     * @param consumerJson  empfangenes Json
     * @param replyTemplate ist das template, dass die zu ersetzenden Werte enthält
     * @return gibt den Ersetzten Json String zurück
     */
    public String replaceTemplate(String consumerJson, String replyTemplate) {
        try {
            String jsonTemplate = readResource(replyTemplate);
            JsonNode jsonNode = new ObjectMapper().readValue(consumerJson, JsonNode.class);
            Handlebars handlebars = new Handlebars();
            handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
            Context context = Context
                    .newBuilder(jsonNode)
                    .resolver(JsonNodeValueResolver.INSTANCE,
                              JavaBeanValueResolver.INSTANCE,
                              FieldValueResolver.INSTANCE,
                              MapValueResolver.INSTANCE,
                              MethodValueResolver.INSTANCE)
                    .build();
            Template template = handlebars.compileInline(jsonTemplate);
            return template.apply(context);
        } catch (IOException e) {
            log.error("Error occurred during template replacement", e);
            throw new TemplateException(e);
        }
    }

    private String readResource(String fileName) {
        try {
            Resource resource = resourceLoader.getResource(replyLocation + "/" + fileName);
            Reader reader = new InputStreamReader(resource.getInputStream());
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            log.error("Error occurred during read template:{}", fileName, e);
            throw new TemplateException(e);
        }
    }
}
