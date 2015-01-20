package org.ldp4j.generic.handlers;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.*;
import org.ldp4j.generic.rdf.RdfUtils;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class BasicContainerCreateHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(BasicContainerCreateHandler.class);

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {
        String containerURI = context.getProperty(LDPContext.REQUEST_URL);
        if(!containerURI.endsWith("/")){
            throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, "The container URI doesn't end with a slash");
        }

        //Generate the URI for the newly created resource
        String newURI = null;

        //Check there is a slug header
        String slug = HttpUtils.getHeaderValue("Slug", context);
        if(slug != null) {
            logger.debug("Slug header found - '{}'", slug);
        }

        newURI = containerURI + slug;

        int counter = 0;
        // Check whether the URI already exists
        while(RdfUtils.resourceExists(newURI)) {
            newURI = containerURI + slug + counter++;
            logger.trace("Checking whether resource {} already exists ...", newURI);
        }

        String metaURI = newURI.replaceFirst("^https?://", "ldp4j://");

        logger.debug("A new URI {} is minted ...", newURI);
        logger.debug("Metadata URI for '{}' is '{}'", newURI, metaURI);

        // Build the new resource model from the body

        String contentTypeHeader = HttpUtils.getHeaderValue(HttpHeader.CONTENT_TYPE.value(), context);
        logger.debug("Processing the content type header '{}'", contentTypeHeader);

        MediaType contentType = MediaTypeUtils.toMediaType(contentTypeHeader);

        String format = RdfUtils.getRdfFormat(contentType);

        if(format != null){
            Model model = ModelFactory.createDefaultModel();
            try {
                model.read(context.getServletRequest().getInputStream(), newURI, format);

                Dataset dataset = ConfigManager.getDataset();
                dataset.begin(ReadWrite.WRITE) ;
                try {
                    dataset.addNamedModel(newURI, model);


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



        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    private Model createMetaMode(String metaURI) {

        Model metaModel = ModelFactory.createDefaultModel();
        Resource resource = metaModel.createResource(metaURI);
        resource.addProperty(RDF.type, LDP.RDFSource);

        return metaModel;

    }
}
