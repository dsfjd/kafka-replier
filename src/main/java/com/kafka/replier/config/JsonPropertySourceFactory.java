package com.kafka.replier.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

public class JsonPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<StubProperties> createPropertySource(
            String name, EncodedResource resource)
            throws IOException {
        StubProperties val = new ObjectMapper()
                .readValue(resource.getInputStream(), StubProperties.class);
        return new JsonToTypedPropertyClass("json-property", val);
    }
}