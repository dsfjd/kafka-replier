package com.kafka.replier.config;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;

import static com.kafka.replier.fixture.StubPropertyFixture.stubProperties;
import static org.assertj.core.api.Assertions.assertThat;

class JsonPropertySourceFactoryTest {

    JsonPropertySourceFactory jsonPropertySourceFactory;

    @BeforeEach
    void setUp() {
        jsonPropertySourceFactory = new JsonPropertySourceFactory();
    }

    @Test
    @SneakyThrows
    void thatCreatePropertySourceWorksCorrectly() {
        EncodedResource encodedResource = new EncodedResource(new ClassPathResource("stub-examples/invalid-all-setup.json"));
        StubProperties expectedResult = stubProperties();

        PropertySource<StubProperties> result = jsonPropertySourceFactory.createPropertySource("someName", encodedResource);

        assertThat(result)
                .extracting(PropertySource::getSource, PropertySource::getName)
                .containsExactly(expectedResult, "json-property");
    }
}