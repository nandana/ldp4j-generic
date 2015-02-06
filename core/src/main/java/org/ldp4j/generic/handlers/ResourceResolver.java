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

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpMethod;
import org.ldp4j.generic.http.HttpStatus;
import static org.ldp4j.generic.util.RdfUtils.resource;

import org.ldp4j.generic.rdf.vocab.LDP4J;
import org.ldp4j.generic.util.LDP4JUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * <p>Resource resolver handler checks if the resource exists in the dataset.</p>
 * <ul>
 *     <li>Retrieves the request url and sets it as the <code>LDPContext.REQUEST_URL</code> property.</li>
 *     <li>Generates the metadata URI and sets it as the <code>LDPContext.META_URL</code> property.</li>
 *     <li>If the metadata graph is not found in the dataset, responds with a not found. (In the special case of PUT,
 *     the process continues and the flag is set in the context to indicate that it is a put to create request.</li>
 *     <li>Sets the resource type based on the metamodel data.</li>
 * </ul>
 *
 */
public class ResourceResolver implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(ResourceResolver.class);

    private static final String NAME = "ResourceResolver";


    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletRequest request = context.getServletRequest();

        String url = request.getRequestURL().toString();
        logger.debug("Request URL: {}", url);

        String metaURL = LDP4JUtils.toMetadataURI(url);
        logger.debug("Metadata URI: {}", metaURL);

        context.setProperty(LDPContext.REQUEST_URL, url);
        context.setProperty(LDPContext.META_URL, metaURL);

        Dataset dataset = ConfigManager.getDataset();

        Model dataModel;
        Model metaModel;
        Resource type;


        dataset.begin(ReadWrite.READ) ;
        try {

            // Resource not found in the dataset
            if (!dataset.containsNamedModel(metaURL)) {
                if (! HttpMethod.PUT.name().equals(request.getMethod())) {
                    logger.error("Resource '{}' not found in the dataset", metaURL);
                    throw new LDPFault(HttpStatus.NOT_FOUND);
                } else {
                    //In the case of PUT, this can be PutToCreate function
                    context.setPutToCreate(true);
                    return HandlerResponse.CONTINUE;
                }

            }

            dataModel = dataset.getNamedModel(url);
            metaModel = dataset.getNamedModel(metaURL);

            NodeIterator typeIterator = metaModel.listObjectsOfProperty(resource(url), RDF.type);
            // At the moment we consider we only have one type in the server managed metadata graph. We assume that
            // on the most specific type is included in the metadata graph.
            if(typeIterator.hasNext()) {
                type = typeIterator.next().asResource();
                logger.debug("'Resource type: {}'", type.getURI());
                while (typeIterator.hasNext()) {
                    Resource secondType = typeIterator.next().asResource();
                    if (!type.equals(secondType)) {
                        throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Two non-identical types '{}', '{}' were" +
                                "found for the resource '{}' . Metadata graph should contain exactly one type.", type.getURI(), secondType.getURI(), url));
                    }
                }
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                metaModel.write(os, "TURTLE");
                logger.error("No type found in the metadata graph \n {}.", os.toString());
                throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, String.format("No type found in the metadata graph for '{}'.", url));
            }

            //TODO validate the resource type. It has to be a valid type
            context.setResourceType(type);

            NodeIterator etagIterator = metaModel.listObjectsOfProperty(resource(url), LDP4J.etag);
            // At the moment we keep the etag as a version number and update it based on the update
            if(etagIterator.hasNext()) {
                int etag = etagIterator.next().asLiteral().getInt();
                context.setEntityTag(etag);
                if (etagIterator.hasNext()){
                    throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Multiple entity tag values found '{}', '{}'.",
                            etag, etagIterator.next().asLiteral().getInt()));
                }
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                metaModel.write(os, "TURTLE");
                logger.error("Entity tag not found in the metadata graph \n {}.", os.toString());
                throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Entity tag not found in the metadata graph for '{}'.", url));
            }

        } finally {
            dataset.end() ;
        }

        if(metaModel == null) {
            throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Metadata model {} not found for the URI {}", metaURL, url));
        }

        if (dataModel != null) {
            context.setDataModel(dataModel);
        }


        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
