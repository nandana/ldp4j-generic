package org.ldp4j.generic.handlers;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.http.Link;
import org.ldp4j.generic.http.LinkBuilderImpl;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class RequestPreProcessor implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(RequestPreProcessor.class);

    private static final Set<String> CONTAINER_METHODS = ImmutableSet.of("GET", "HEAD", "OPTIONS", "PUT", "POST");

    private static final Set<String> RESOURCE_METHODS = ImmutableSet.of("GET", "HEAD", "OPTIONS", "PUT");

    private static final Set<String> INTERACTION_MODELS = ImmutableSet.of(LDP.Resource.getURI(),
            LDP.BasicContainer.getURI(), LDP.DirectContainer.getURI(), LDP.IndirectContainer.getURI());

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletRequest request = context.getServletRequest();
        Resource type = context.getResourceType();

        // Check if the method is allowed
        String method = request.getMethod();
        context.setProperty(LDPContext.METHOD, method);
        logger.debug("Request method is set to {}", method);

        if(LDP.RDFSource.equals(type) && !RESOURCE_METHODS.contains(method)){
            logger.error("Method {} is not allowed for LDP RDF Sources. Allowed methods are {}.", method, Joiner.on(",").join(RESOURCE_METHODS));
            throw new LDPFault(HttpStatus.METHOD_NOT_ALLOWED);
        } else if ( (LDP.BasicContainer.equals(type) || LDP.DirectContainer.equals(type) || LDP.IndirectContainer.equals(type))
                && !CONTAINER_METHODS.contains(method) ){
            logger.error("Method {} is not allowed for LDP Containers. Allowed methods are {}.", method , Joiner.on(",").join(CONTAINER_METHODS));
            throw new LDPFault(HttpStatus.METHOD_NOT_ALLOWED);
        }

        //Look for the Link relation header
        List<Link> links = new ArrayList<Link>();
        Enumeration<String> values = request.getHeaders("Link");
        while(values.hasMoreElements()){
            List<Link> temp = Link.parse(values.nextElement());
            links.addAll(temp);
        }

        logger.debug("'{}' Link header(s) found", links.size());
        String interactionModel = null;
        for(Link link : links){
            String rel = link.getRel();
            if ("type".equals(rel) && interactionModel == null) {
                interactionModel = link.getUri().toString();
            } else {
                logger.debug("Multiple interaction patterns found: {} {}", interactionModel, link.getUri().toString());
                throw new LDPFault(HttpStatus.BAD_REQUEST, "Multiple interaction models in the request.");
            }
        }
        if(interactionModel != null){
            if (!INTERACTION_MODELS.contains(interactionModel)) {
                logger.debug("Unknown interaction model: {}", interactionModel);
                throw new LDPFault(HttpStatus.BAD_REQUEST, "Unknown interaction model: " + interactionModel);
            }
            logger.debug("{} interaction model found.");
            context.setProperty(LDPContext.INTERACTION_MODEL, interactionModel);
        }

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return null;
    }
}
