package com.kafka.replier.exception;

import com.kafka.replier.config.StubProperties;

import static com.kafka.replier.config.StubProperties.StubEntry;
import static java.lang.String.format;

/**
 * Diese Exception wird geworfen, wenn es zirkuläre Definitionen der {@link StubProperties} gibt.
 */
public class CircularReferenceException extends RuntimeException {

    /**
     * Konstruktor
     *
     * @param e1 StubEntry 1
     * @param e2 StubEntry 2
     */
    public CircularReferenceException(StubEntry e1, StubEntry e2) {
        super(format("Circular reference found in %n entry:%s and %n entry:%s", e1, e2));
    }
}
