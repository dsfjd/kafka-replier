package com.kafka.replier.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.kafka.replier.fixture.StubPropertyFixture.stubProperties;
import static org.assertj.core.api.Assertions.assertThat;

class JsonToTypedPropertyClassTest {

    JsonToTypedPropertyClass jsonToTypedPropertyClass;

    @BeforeEach
    void setUp() {
        jsonToTypedPropertyClass = new JsonToTypedPropertyClass("someName", stubProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "unknown", "value" })
    void thatGetPropertyReturnsNullForUnknownProperty(String property) {
        Object result = jsonToTypedPropertyClass.getProperty(property);

        assertThat(result).isNull();
    }

    @Test
    void thatGetPropertyReturnsTheCorrectProperty() {
        Object result = jsonToTypedPropertyClass.getProperty("stubs");

        assertThat(result).isEqualTo(stubProperties().getStubs());
    }
}