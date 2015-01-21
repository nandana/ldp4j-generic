package org.ldp4j.generic.rdf;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdfUtils {

    private static final Logger logger = LoggerFactory.getLogger(RdfUtils.class);

    private static Model m_model = ModelFactory.createDefaultModel();

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

    public static Resource resource(String resourceURI) {
        return m_model.createResource(resourceURI);
    }

    public static Resource property(String propertyURI) {
        return m_model.createProperty(propertyURI);
    }

}
