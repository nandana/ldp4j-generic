package org.ldp4j.generic.core;

public interface Handler {

    public HandlerResponse invoke(LDPContext context) throws LDPFault;

    public String getName();
}
