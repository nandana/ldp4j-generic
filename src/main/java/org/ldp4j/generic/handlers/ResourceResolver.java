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
import org.ldp4j.generic.http.HttpStatus;
import static org.ldp4j.generic.util.RdfUtils.resource;

import org.ldp4j.generic.rdf.vocab.LDP4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Check if the resource exists in the dataset.
 */
public class ResourceResolver implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(ResourceResolver.class);

    private static final String NAME = "ResourceResolver";


    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletRequest request = context.getServletRequest();

        String url = request.getRequestURL().toString();
        logger.debug("Request URL: {}", url);

        String scheme = request.getScheme();
        String metaURL = getMetadataURI(url, scheme);
        logger.debug("Metadata URI: {}", metaURL);

        context.setProperty(LDPContext.REQUEST_URL, url);

        Dataset dataset = ConfigManager.getDataset();

        Model dataModel;
        Model metaModel;
        dataset.begin(ReadWrite.READ) ;
        Resource type;
        try {

            // Resource not found in the dataset
            if (!dataset.containsNamedModel(metaURL) && !"PUT".equals(request.getMethod())) {
                logger.error("Resource '{}' not found in the dataset", metaURL);
                throw new LDPFault(HttpStatus.NOT_FOUND);
            }

            dataModel = dataset.getNamedModel(url);
            metaModel = dataset.getNamedModel(metaURL);

            Map<String, String> nsPrefixMap = dataModel.getNsPrefixMap();
            System.out.println("Size:" + nsPrefixMap.size());
            for (Map.Entry<String, String> entry : nsPrefixMap.entrySet()) {
                System.out.println( entry.getKey()+ ":" + entry.getValue());
            }


            NodeIterator typeIterator = metaModel.listObjectsOfProperty(resource(url), RDF.type);
            // At the moment we consider we only have one type in the server managed metadata graph. We assume that
            // on the most specific type is included in the metadata graph.
            if(typeIterator.hasNext()) {
                type = typeIterator.next().asResource();
                logger.debug("'Resource type of '{}' is resolved to '{}'", url, type.getURI());
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

            NodeIterator etagIterator = metaModel.listObjectsOfProperty(resource(url), LDP4J.etag);
            // At the moment we consider we only have one type in the server managed metadata graph. We assume that
            // on the most specific type is included in the metadata graph.
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

            context.setResourceType(type);

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

    private String getMetadataURI(String uri, String scheme){

        return uri.replaceFirst(scheme, "ldp4j");

    }
}
