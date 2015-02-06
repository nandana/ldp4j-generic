package org.ldp4j.generic.handlers;

import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.ldp4j.generic.util.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

public class OptionsHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(OptionsHandler.class);

    private static final String NAME = "OptionsHandler";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletResponse response = context.getServletResponse();

        Resource type = context.getResourceType();
        if(LDP.BasicContainer.equals(type) || LDP.DirectContainer.equals(type) || LDP.IndirectContainer.equals(type)) {
            //TODO check if resource specific rules exist
            response.addHeader(HttpHeader.ALLOW.header(), "HEAD, OPTIONS, GET, POST, PUT, DELETE");
            response.addHeader(HttpHeader.ACCEPT_POST.header(), RdfUtils.getSupportedRdfMediaTypes());

        } else if (LDP.Resource.equals(type)) {
            //TODO check if resource specific rules exist
            response.addHeader(HttpHeader.ALLOW.header(), "HEAD, OPTIONS, GET, PUT, DELETE");
        } else {
            String msg = String.format("Invalid resource type {}", type.getURI());
            logger.error(msg);
            throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, msg);
        }

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
