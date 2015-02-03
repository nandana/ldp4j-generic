package org.ldp4j.generic.http;

public enum HttpStatus {
    CONTINUE(100, "Continue"),
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406,"Not Acceptable"),
    CONDITION_FAILED(412, "Condition Failed"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type");

    private final int statusCode;

    private final String description;

    HttpStatus(int statusCode, String description){
        this.statusCode = statusCode;
        this.description = description;
    }

    public int code(){
        return  statusCode;
    }

    public String description(){
        return description;
    }
}
