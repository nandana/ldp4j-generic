/**
 * Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid
 * (http://www.oeg-upm.net/)
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

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.commons.io.IOUtils;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.config.DefaultPropertyConfig;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.*;
import org.ldp4j.generic.util.HttpUtils;
import org.ldp4j.generic.ldp.runtime.BasicContainerStrategy;
import org.ldp4j.generic.ldp.runtime.ContainerStrategy;
import org.ldp4j.generic.ldp.runtime.DirectContainerStrategy;
import org.ldp4j.generic.ldp.runtime.IndirectContainerStrategy;
import org.ldp4j.generic.util.LDP4JUtils;
import org.ldp4j.generic.util.MediaTypeUtils;
import org.ldp4j.generic.util.RdfUtils;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.ldp4j.generic.rdf.vocab.LDP4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Calendar;
import java.util.Date;

import static org.ldp4j.generic.util.RdfUtils.resource;

public class PostHandler implements Handler {

    private static final String NAME = "PostHandler";

    private static final Logger logger = LoggerFactory.getLogger(PostHandler.class);

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        ContainerStrategy strategy;

        if(LDP.BasicContainer.equals(context.getResourceType())){
            strategy = new BasicContainerStrategy();
        } else if (LDP.DirectContainer.equals(context.getResourceType())) {
            strategy = new DirectContainerStrategy();
        } else if (LDP.IndirectContainer.equals(context.getResourceType())) {
            strategy = new IndirectContainerStrategy();
        } else {
            return HandlerResponse.CONTINUE;
        }

        String containerURI = context.getProperty(LDPContext.REQUEST_URL);
        if(!containerURI.endsWith("/")){
            String msg = "The container URI doesn't end with a slash";
            logger.error(msg);
            throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, msg);
        }


        //TODO probably this logic can be improved, take a look later
        //Generate the URI for the newly created resource
        String newURI = null;

        //Check there is a slug header
        String slug = HttpUtils.getHeaderValue("Slug", context);
        if(slug != null) {
            logger.debug("Slug header found - '{}'", slug);
            newURI = containerURI + slug;
        } else {
            slug = "";
            newURI = containerURI + "1";
        }

        boolean isContainer = false;
        String interactionModel = context.getProperty(LDPContext.INTERACTION_MODEL);
        if(interactionModel != null &&
                LDP4JUtils.isContainer(RdfUtils.resource(interactionModel))){
            isContainer = true;
            if(!newURI.endsWith("/")) {
                newURI = newURI + "/";
            }
        }

        int counter = 1;
        // Check whether the URI already exists
        while(RdfUtils.resourceExists(newURI)) {
            logger.trace("Resource '{}' already exists ...", newURI);
            newURI = containerURI + slug + ++counter;
            if(isContainer){
                newURI = newURI + "/";
            }
            logger.trace("Checking if resource '{}' already exists ...", newURI);
        }

        String metaURI = LDP4JUtils.toMetadataURI(newURI);

        logger.debug("A new URI {} is minted ...", newURI);
        logger.debug("Metadata URI for '{}' is '{}'", newURI, metaURI);

        // Build the new resource model from the body

        String contentTypeHeader = HttpUtils.getHeaderValue(HttpHeader.CONTENT_TYPE.header(), context);
        logger.debug("Processing the content type header '{}'", contentTypeHeader);

        MediaType contentType = MediaTypeUtils.toMediaType(contentTypeHeader);

        String format = RdfUtils.getRdfFormat(contentType);

        if(format != null){
            Model model = ModelFactory.createDefaultModel();
            try {

                StringWriter writer = new StringWriter();
                IOUtils.copy(context.getServletRequest().getInputStream(), writer, "utf-8");
                String content = writer.toString();

                logger.trace("Resource content \n {}", content);

                Reader reader = new StringReader(content);
                model.read(reader, newURI, format);

                DefaultPropertyConfig defaultPropertyConfig = ConfigManager.getDefaultPropertyConfig();

                if (defaultPropertyConfig.isCreated()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    model.add(resource(newURI), DCTerms.created, model.createTypedLiteral(cal));
                }

                // Create the new resource
                Dataset dataset = ConfigManager.getDataset();
                dataset.begin(ReadWrite.WRITE) ;
                try {
                    Model container = dataset.getNamedModel(containerURI);
                    //Add the containment triple
                    container.add(resource(containerURI), LDP.contains, resource(newURI));

                    //Add the membership triple
                    strategy.addMemberTriple(containerURI, newURI, model, container);

                    dataset.addNamedModel(newURI, model);
                    dataset.addNamedModel(metaURI, createMetaMode(newURI, containerURI, context));
                    dataset.commit() ;
                } finally {
                    dataset.end() ;
                }

            } catch (IOException e) {
                throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading the request." ,e);
            }
        } else {
            throw new LDPFault(HttpStatus.UNSUPPORTED_MEDIA_TYPE, String.format("Content type {} is not supported.",
                    contentType));
        }

        HttpServletResponse response = context.getServletResponse();
        response.setStatus(HttpStatus.CREATED.code());
        response.setHeader(HttpHeader.LOCATION.header(), newURI);

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    private Model createMetaMode(String newURI, String containerURI, LDPContext context) {

        String interactionModel = context.getProperty(LDPContext.INTERACTION_MODEL);
        //If the interaction model is not specified, we will use the resource interaction model as default
        //TODO may be we can analyze the content to check the type instead of defaulting to LDPR
        if(interactionModel == null){
            interactionModel = LDP.Resource.getURI();
        }

        Model metaModel = ModelFactory.createDefaultModel();
        Resource resource = metaModel.createResource(newURI);
        resource.addProperty(RDF.type, RdfUtils.resource(interactionModel));
        resource.addLiteral(LDP4J.etag, 1);
        resource.addProperty(LDP4J.parent, RdfUtils.resource(containerURI));

        return metaModel;

    }
}
