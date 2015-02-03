package org.ldp4j.generic.handlers;

import com.google.common.base.Joiner;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

public class RequestPostProcessor implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(RequestPostProcessor.class);

    private static final String NAME = "RequestPostProcessor";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletResponse response = context.getServletResponse();
        String method = context.getProperty(LDPContext.METHOD);

        //Set the entity tag
        if(!"OPTIONS".equals(method) || !"DELETE".equals(method)) {
            int etag = context.getEntityTag();
            response.setHeader(HttpHeader.ETAG.value(), "\"" + Integer.toString(etag) + "\"" );
        }

        //Set the LDP headers
        Resource type = context.getResourceType();
        response.setHeader(HttpHeader.LINK.value(), getTypeLinkHeader(type));

        //Set CORS headers
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN.value(), "*");
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_HEADERS.value(), "Accept, Content-Type, If-Match, Link");
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_METHODS.value(), "HEAD, OPTIONS, GET, PUT, POST");
        response.setHeader(HttpHeader.ACCESS_CONTROL_EXPOSE_HEADERS.value(), Joiner.on(",").join(response.getHeaderNames()));

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    private String getTypeLinkHeader(Resource type) {
        if (LDP.BasicContainer.equals(type) || LDP.DirectContainer.equals(type) || LDP.IndirectContainer.equals(type)  ){
            return String.format("<%s>; rel=\"type\", <http://www.w3.org/ns/ldp#Resource>; rel=\"type\"", type.getURI());
        } else {
            return "<http://www.w3.org/ns/ldp#Resource>; rel='type'";
        }
    }
}
