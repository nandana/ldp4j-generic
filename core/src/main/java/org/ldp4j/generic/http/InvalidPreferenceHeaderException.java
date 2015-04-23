package org.ldp4j.generic.http;

public class InvalidPreferenceHeaderException extends RuntimeException {

    private static final long serialVersionUID = -2179796197876469324L;

    public InvalidPreferenceHeaderException() {
        super();
    }

    public InvalidPreferenceHeaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPreferenceHeaderException(String message) {
        super(message);
    }

    public InvalidPreferenceHeaderException(Throwable cause) {
        super(cause);
    }
}
