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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.typesafe.config.Config;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.*;
import org.ldp4j.generic.ldp.runtime.BasicContainerStrategy;
import org.ldp4j.generic.ldp.runtime.ContainerStrategy;
import org.ldp4j.generic.ldp.runtime.DirectContainerStrategy;
import org.ldp4j.generic.ldp.runtime.IndirectContainerStrategy;
import org.ldp4j.generic.rdf.vocab.HttpMethods;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.ldp4j.generic.util.MediaTypeUtils;
import org.ldp4j.generic.util.PreferHeaderUtils;
import org.ldp4j.generic.util.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class ResponseRDFWriter implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseRDFWriter.class);

    private static final String NAME = "ResponseRDFWriter";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        Model dataModel = context.getDataModel();

        if (dataModel == null) {
            logger.debug("Data model not found.");
            return HandlerResponse.CONTINUE;
        }

        HttpServletResponse response = context.getServletResponse();

        Resource type = context.getResourceType();
        if(LDP.BasicContainer.equals(type) || LDP.DirectContainer.equals(type) || LDP.IndirectContainer.equals(type)) {
            RepresentationPreference preference = context.getRepresentationPreference();
            if (preference != null) {

                ContainerStrategy strategy = null;

                if(LDP.BasicContainer.equals(context.getResourceType())){
                    strategy = new BasicContainerStrategy();
                } else if (LDP.DirectContainer.equals(context.getResourceType())) {
                    strategy = new DirectContainerStrategy();
                } else if (LDP.IndirectContainer.equals(context.getResourceType())) {
                    strategy = new IndirectContainerStrategy();
                }

                String containerURI = context.getProperty(LDPContext.REQUEST_URL);

                dataModel = strategy.getPreferredRepresentation(containerURI, dataModel, preference);
                response.addHeader(HttpHeader.PREFERENCE_APPLIED.header(), PreferHeaderUtils.asPreferenceAppliedHeader(preference));

            }
        }

        HttpServletRequest request = context.getServletRequest();

        String accept = request.getHeader(HttpHeader.ACCEPT.header());
        logger.debug("Processing the accept header '{}'", accept);

        List<MediaType> mediaTypes = MediaTypeUtils.parseMediaTypes(accept);

        // Check for supported media types
        String format = null;
        MediaType contentType = null;
        for (MediaType mediaType: mediaTypes) {
            if (MediaType.TURTLE.isCompatible(mediaType)) {
                format = "TURTLE";
                contentType = MediaType.TURTLE;
                logger.debug("Content type set to Turtle.");
                break;
            } else if (MediaType.JSON_LD.isCompatible(mediaType)) {
                format = "JSON-LD";
                contentType = MediaType.JSON_LD;
                logger.debug("Content type set to JSON-LD.");
                break;
            } else if (MediaType.RDF_XML.isCompatible(mediaType)) {
                format = "RDF/XML";
                contentType = MediaType.RDF_XML;
                logger.debug("Content type set to RDF/XML.");
                break;

            // This is to handle the custom html views
            } else if (MediaType.HTML.isCompatible(mediaType)) {

                logger.debug("Content type set to HTML.");

                Config appConfig = ConfigManager.getAppConfig(false);
                if(appConfig.getBoolean("html-view.enabled")) {
                    logger.debug("HTML view is enabled.");

                    String requestURI =  context.getProperty(LDPContext.REQUEST_URL);

                    String urlPrefix;
                    NodeIterator iterator = dataModel.listObjectsOfProperty(dataModel.createResource(requestURI),
                            RDF.type);

                    for(; iterator.hasNext(); ){
                        RDFNode node = iterator.next();
                        if (node.isURIResource()) {

                            //TODO It is better to use the full URI instead of the local name
                            String localName = node.asResource().getLocalName();

                            String path = "html-view.map."+localName;
                            logger.debug("Looking for the html view map for the key '{}'", path);

                            if (appConfig.hasPath(path)) {
                                urlPrefix =  appConfig.getString(path);

                                String redirectUrl = urlPrefix + requestURI;

                                response.setStatus(HttpStatus.SEE_OTHER.code());
                                response.setHeader("Location", redirectUrl);

                                logger.debug("Redirecting to the request to {}", redirectUrl);

                                return HandlerResponse.ABORT;
                            }

                        }
                    }

                }

            }
        }

        if (format == null) {
            throw new LDPFault(HttpStatus.NOT_ACCEPTABLE, String.format("The accept header {} characteristics can not be " +
                        "fulfilled. Supported media types are : text/turtle, application/ld+json, and application/rdf+xml"));
        }

        context.setProperty(LDPContext.RESP_CONTENT_TYPE, contentType.getValue());

        try {
            //TODO properly set the character encoding
            response.setHeader(HttpHeader.CONTENT_TYPE.header(), contentType.getValue()+"; charset=utf-8");

            if (HttpMethod.HEAD.equals(context.getMethod())) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                dataModel.write(baos);
                response.setContentLength(baos.size());
            } else {
                ServletOutputStream outputStream = response.getOutputStream();
                dataModel.write(outputStream, format);
            }


        } catch (IOException e) {
            throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, "Error while getting the output stream", e);
        }

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return NAME;
    }


}
