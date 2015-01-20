package org.ldp4j.generic.http;

public enum HttpStatus {
    CONTINUE(100, "Continue"),
    OK(200, "OK"),
    NOT_ACCEPTABLE(406,"Not Acceptable"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type");

    private final int statusCode;

    private final String description;

    HttpStatus(int statusCode, String description){
        this.statusCode = statusCode;
        this.description = description;
    }
}
