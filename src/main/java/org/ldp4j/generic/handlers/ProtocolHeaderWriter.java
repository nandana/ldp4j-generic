package org.ldp4j.generic.handlers;

import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.http.HttpUtils;
import org.ldp4j.generic.rdf.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolHeaderWriter implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolHeaderWriter.class);

    private static final String NAME = "ProtocolHeaderWriter";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
