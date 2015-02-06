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
package org.ldp4j.generic.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Set;

public class RdfUtils {

    private static final Logger logger = LoggerFactory.getLogger(RdfUtils.class);

    private static Model m_model = ModelFactory.createDefaultModel();

    private static final Set<MediaType> RDF_MEDIA_TYPES = ImmutableSet.of(MediaType.TURTLE, MediaType.JSON_LD, MediaType.RDF_XML);

    public static boolean resourceExists(String graphName){

        logger.trace("Checking if the resource {} exists ...", graphName);

        Dataset dataset = ConfigManager.getDataset();

        dataset.begin(ReadWrite.READ) ;
        try {
            return dataset.containsNamedModel(graphName);
        } finally {
            dataset.close();
        }

    }

    public static String getRdfFormat(MediaType mediaType) {

        String format = null;

        if (MediaType.TURTLE.isCompatible(mediaType)) {
            format = "TURTLE";
        } else if (MediaType.JSON_LD.isCompatible(mediaType)) {
            format = "JSON-LD";
        } else if (MediaType.RDF_XML.isCompatible(mediaType)) {
            format = "RDF/XML";
        }

        return format;

    }

    public static String getSupportedRdfMediaTypes() {

        return Joiner.on(",").join(RDF_MEDIA_TYPES);

    }

    public static Resource resource(String resourceURI) {
        return m_model.createResource(resourceURI);
    }

    public static Property property(String propertyURI) {
        return m_model.createProperty(propertyURI);
    }

    public static String toString(Model model){
        return toString(model, "TURTLE");
    }

    public static String toString(Model model, String lang){
        StringWriter writer = new StringWriter();
        model.write(writer, lang);
        return writer.toString();
    }



}
