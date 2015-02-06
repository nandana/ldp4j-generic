/**
 * Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid (http://www.oeg-upm.net/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ldp4j.generic.handlers;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpMethod;
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

/***
 * <p> Request pre processor extracts generic headers from the request and set them on the context</p>
 * <ul>
 *     <ul> Check if the method is allowed </ul>
 *     <ul>Extracts the interaction model </ul>
 * </ul>
 */
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
        context.setMethod(HttpMethod.valueOf(method));
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
            logger.debug("{} interaction model found.", interactionModel);
            context.setProperty(LDPContext.INTERACTION_MODEL, interactionModel);
        } else {
            logger.debug("No interaction model found in the request.");
        }

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return null;
    }
}
