package org.ldp4j.generic.core;

import org.ldp4j.generic.http.HttpStatus;

public class LDPFault extends Exception {

    // A flag that indicates whether it is possible to convert the fault into a meaningful HTTP error
    private boolean processable = false;

    // The corresponding HTTP status code for the fault
    private HttpStatus statusCode;

    public LDPFault(String msg){
        super(msg);
    }

    public LDPFault(String msg, Throwable e){
        super(msg, e);
    }

    public LDPFault(HttpStatus status) {
        statusCode = status;
        processable = true;

    }

    public LDPFault(HttpStatus status, String msg) {
        super(msg);
        statusCode = status;
        processable = true;
    }

    public LDPFault(HttpStatus status, String msg, Throwable e) {
        super(msg, e);
        statusCode = status;
        processable = true;
    }

    public boolean isProcessable(){
        return processable;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
