package com.kafka.replier.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;

/**
 * Property Source für {@link StubProperties}
 */
@Slf4j
class JsonToTypedPropertyClass extends PropertySource<StubProperties> {

    private static final String PROPERTY = "stubs";

    public JsonToTypedPropertyClass(String name, StubProperties stubProperties) {
        super(name, stubProperties);
    }

    @Override
    public Object getProperty(String name) {
        if (!PROPERTY.equals(name)) {
            return null;
        }
        try {
            return this.source.getClass().getDeclaredField(name).get(this.source);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Failed To getProperty:{}", name, e);
            return null;
        }
    }
}
