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
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpMethod;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Set;

public class RequestPostProcessor implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(RequestPostProcessor.class);

    private static final String NAME = "RequestPostProcessor";

    private static final Set<String> ALLOW_METHODS = ImmutableSet.of("HEAD", "OPTIONS", "GET", "PUT", "POST", "DELETE");

    private static final Set<String> ALLOW_HEADERS = ImmutableSet.of("Accept", "Content-Type", "If-Match", "Link");

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletResponse response = context.getServletResponse();
        HttpMethod method = context.getMethod();

        //Set the entity tag
        if(!HttpMethod.OPTIONS.equals(method) || !HttpMethod.DELETE.equals(method)) {
            int etag = context.getEntityTag();
            response.setHeader(HttpHeader.ETAG.header(), "\"" + Integer.toString(etag) + "\"" );
        }

        //Set the LDP headers
        Resource type = context.getResourceType();
        response.setHeader(HttpHeader.LINK.header(), getTypeLinkHeader(type));

        //Set CORS headers
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN.header(), "*");
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_HEADERS.header(), Joiner.on(", ").join(ALLOW_HEADERS));
        response.setHeader(HttpHeader.ACCESS_CONTROL_ALLOW_METHODS.header(), Joiner.on(", ").join(ALLOW_METHODS));
        response.setHeader(HttpHeader.ACCESS_CONTROL_EXPOSE_HEADERS.header(), Joiner.on(", ").join(response.getHeaderNames()));

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
            return "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"";
        }
    }
}
