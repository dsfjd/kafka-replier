package com.kafka.replier.exception;

/**
 * Wird geworfen, wenn ein Template nicht ersetzt werden kann
 */
public class TemplateException extends RuntimeException {

    /**
     * Konstruktor
     *
     * @param e Exception
     */
    public TemplateException(Exception e) {
        super(e);
    }
}
