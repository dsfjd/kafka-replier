package com.kafka.replier.exception;

/**
 * Wird geworfen, wenn die Listener nicht erstellt werden können
 */
public class KafkaSetupListenerException extends RuntimeException {

    /**
     * Konstruktor
     *
     * @param exception Exception
     */
    public KafkaSetupListenerException(Exception exception) {
        super(exception);
    }
}
