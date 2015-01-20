package org.ldp4j.generic.rdf;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nandana on 1/20/15.
 */
public class RdfUtils {

    private static final Logger logger = LoggerFactory.getLogger(RdfUtils.class);

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

}
