package org.ldp4j.generic.handlers;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by nandana on 1/20/15.
 */
public class RequestPreProcessor implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(RequestPreProcessor.class);

    private static final Set<String> CONTAINER_METHODS = ImmutableSet.of("GET, HEAD, OPTIONS, PUT, POST");

    private static final Set<String> RESOURCE_METHODS = ImmutableSet.of("GET, HEAD, OPTIONS, PUT");

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletRequest request = context.getServletRequest();
        Resource type = context.getResourceType();

        // Check if the method is allowed
        String method = request.getMethod();
        context.setProperty(LDPContext.METHOD, method);

        if(LDP.RDFSource.equals(type) && !RESOURCE_METHODS.contains(method)){
            throw new LDPFault(HttpStatus.METHOD_NOT_ALLOWED, "Method {} is not allowed for LDP RDF Sources.");
        } else if ( (LDP.BasicContainer.equals(type) || LDP.DirectContainer.equals(type) || LDP.IndirectContainer.equals(type))
                && !CONTAINER_METHODS.contains(method) ){
            throw new LDPFault(HttpStatus.METHOD_NOT_ALLOWED, "Method {} is not allowed for LDP Containers.");
        }



        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
