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
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.*;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.http.MediaType;
import org.ldp4j.generic.ldp.runtime.BaseContainerStrategy;
import org.ldp4j.generic.rdf.vocab.LDP4J;
import org.ldp4j.generic.util.HttpUtils;
import org.ldp4j.generic.util.LDP4JUtils;
import org.ldp4j.generic.util.MediaTypeUtils;
import org.ldp4j.generic.util.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class PutHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(PutHandler.class);

    private static final String NAME = "PutHandler";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        //TODO check the special handling for LDP Containers

        //TODO consider moving to pre processing, also found in PostOnContainer
        String contentTypeHeader = HttpUtils.getHeaderValue(HttpHeader.CONTENT_TYPE.header(), context);
        logger.debug("Processing the content type header '{}'", contentTypeHeader);

        MediaType contentType = MediaTypeUtils.toMediaType(contentTypeHeader);

        String format = RdfUtils.getRdfFormat(contentType);

        if(format != null){

            String ldprURI = context.getProperty(LDPContext.REQUEST_URL);
            Model model = ModelFactory.createDefaultModel();
            try {
                model.read(context.getServletRequest().getInputStream(), ldprURI, format);
            } catch (IOException e) {
                logger.error("Error reading the request body ...", e);
                throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading the request." ,e);
            }

            // Check the entity tags
            int currentEtag = context.getEntityTag();
            int ifMatch = extractIfMatchValue(context);
            if (currentEtag != ifMatch) {
                String msg = String.format("Current entity tag '%s' doest match with the If-Match header '%s'.",
                        currentEtag, ifMatch);
                logger.error(msg);
                throw new LDPFault(HttpStatus.CONDITION_FAILED, msg);
            } else {
                currentEtag++;
                context.setEntityTag(currentEtag);
                logger.debug("Set the new etag to {} ...", currentEtag);
            }

            Dataset dataset = ConfigManager.getDataset();
            dataset.begin(ReadWrite.WRITE) ;
            try {

                Model oldModel = dataset.getNamedModel(ldprURI);
                Set<Property> changes = readOnlyPropertyChanges(oldModel, model, ldprURI);

                if(changes.size() != 0){
                    dataset.abort();
                    HttpServletResponse response = context.getServletResponse();
                    response.setHeader("Link", "<http://www.w3.org/TR/ldp/> rel=\"http://www.w3.org/ns/ldp#constrainedBy\"");

                    String msg = "Request is trying to update the read only properties: " + Joiner.on(", ").join(changes);
                    logger.error(msg);
                    throw new LDPFault(HttpStatus.CONFLICT, msg);
                }

                Set<Property> restricted = checkRestrictedProperties(model, ldprURI);
                if (restricted.size() != 0) {
                    dataset.abort();
                    HttpServletResponse response = context.getServletResponse();
                    response.setHeader("Link", "<http://www.w3.org/TR/ldp/> rel=\"http://www.w3.org/ns/ldp#constrainedBy\"");

                    String msg = "Request is trying to add restricted properties: " + Joiner.on(", ").join(restricted);
                    logger.error(msg);
                    throw new LDPFault(HttpStatus.CONFLICT, msg);
                }

                if (LDP4JUtils.isContainer(context.getResourceType()) &&
                        !BaseContainerStrategy.verifyContainmentTriples(ldprURI, oldModel, model)) {
                    dataset.abort();
                    String msg = "Request is trying to update the containment triples";
                    logger.error(msg);
                    throw new LDPFault(HttpStatus.CONFLICT, msg);
                }


                dataset.replaceNamedModel(ldprURI, model);
                Model metaModel = dataset.getNamedModel(context.getProperty(LDPContext.META_URL));
                Statement property = metaModel.getProperty(RdfUtils.resource(ldprURI), LDP4J.etag);
                model.remove(property);
                property.changeLiteralObject(currentEtag);
                model.add(property);
                dataset.commit();
                logger.debug("Updated the resource '{}'", ldprURI);
            } finally {
                dataset.end() ;
            }

        } else {
            String msg = String.format("Content type {} is not supported.", contentType);
            logger.debug(msg);
            throw new LDPFault(HttpStatus.UNSUPPORTED_MEDIA_TYPE, msg);
        }


        return HandlerResponse.CONTINUE;

    }

    @Override
    public String getName() {
        return NAME;
    }

    private int extractIfMatchValue(LDPContext context) throws LDPFault{

        HttpServletRequest request = context.getServletRequest();
        String etag = request.getHeader("If-Match");
        if (etag == null) {
            throw new LDPFault(HttpStatus.PRECONDITION_REQUIRED);
        } else {
            // Removing the surrounding quotation marks
            etag = etag.replace("\"", "");
            try {
                return Integer.valueOf(etag);
            } catch (NumberFormatException e) {
                logger.error("At the moment, the server expects a number as the etag");
                throw new LDPFault(HttpStatus.CONDITION_FAILED);
            }

        }
    }

    private Set<Property> readOnlyPropertyChanges(Model oldModel, Model newModel, String resource){

        logger.trace("Checking for the changes in read-only properties ...");

        Set<Property> changed = new HashSet<Property>();

        Resource res = oldModel.createResource(resource);

        for (Property p : ConfigManager.getReadOnlyProps()) {
            Node oldValue = oldModel.getProperty(res, p).getObject().asNode();
            Node newValue = newModel.getProperty(res, p).getObject().asNode();

            if(!oldValue.sameValueAs(newValue)){
                changed.add(p);
                logger.debug("Change to read only property detected ... {} [old:{}, new:{}]", p.getURI(), oldValue, newValue);
            }
        }

        return changed;
    }

    private Set<Property> checkRestrictedProperties(Model newModel, String resource) {

        logger.trace("Checking for the restricted properties ...");

        Set<Property> restricted = new HashSet<Property>();

        for (Property p : ConfigManager.getRestrictedProps()){
            if(newModel.contains(RdfUtils.resource(resource), p)) {
                restricted.add(p);
                logger.debug("Restricted property {} detected on resource {}", p.getURI(), resource );
            }
        }

        return  restricted;

    }

}
