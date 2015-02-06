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
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpMethod;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.http.MediaType;
import org.ldp4j.generic.rdf.vocab.HttpMethods;
import org.ldp4j.generic.util.MediaTypeUtils;
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
                break;
            } else if (MediaType.JSON_LD.isCompatible(mediaType)) {
                format = "JSON-LD";
                contentType = MediaType.JSON_LD;
                break;
            } else if (MediaType.RDF_XML.isCompatible(mediaType)) {
                format = "RDF/XML";
                contentType = MediaType.RDF_XML;
                break;
            }
        }

        if (format == null) {
            throw new LDPFault(HttpStatus.NOT_ACCEPTABLE, String.format("The accept header {} characteristics can not be " +
                        "fulfilled. Supported media types are : text/turtle, application/ld+json, and application/rdf+xml"));
        }

        context.setProperty(LDPContext.RESP_CONTENT_TYPE, contentType.getValue());

        HttpServletResponse response = context.getServletResponse();
        try {
            response.setHeader(HttpHeader.CONTENT_TYPE.header(), contentType.getValue());

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
