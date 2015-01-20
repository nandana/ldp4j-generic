package org.ldp4j.generic.handlers;

import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpUtils;
import org.ldp4j.generic.http.MediaType;
import org.ldp4j.generic.http.MediaTypeUtils;
import org.ldp4j.generic.rdf.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nandana on 1/20/15.
 */
public class GenericPreProcessor implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(GenericPreProcessor.class);

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        //Check accepted response types


        //Process the content type



        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
