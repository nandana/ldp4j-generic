package org.ldp4j.generic.handlers;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.http.MediaType;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.ldp4j.generic.rdf.vocab.LDP4J;
import org.ldp4j.generic.util.HttpUtils;
import org.ldp4j.generic.util.MediaTypeUtils;
import org.ldp4j.generic.util.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class PutOnLdprHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(PutOnLdprHandler.class);

    private static final String NAME = "PutOnLdprHandler";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        if(LDP.Resource.equals(context.getResourceType())) {

            //TODO consider moving to pre processing, also found in PostOnContainer
            String contentTypeHeader = HttpUtils.getHeaderValue(HttpHeader.CONTENT_TYPE.value(), context);
            logger.debug("Processing the content type header '{}'", contentTypeHeader);

            MediaType contentType = MediaTypeUtils.toMediaType(contentTypeHeader);

            String format = RdfUtils.getRdfFormat(contentType);

            if(format != null){

                String ldprURI = context.getProperty(LDPContext.REQUEST_URL);
                Model model = ModelFactory.createDefaultModel();
                try {
                    model.read(context.getServletRequest().getInputStream(), ldprURI, format);
                } catch (IOException e) {
                    logger.error("Error reading the request body ...", e);
                    throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading the request." ,e);
                }

                // Check the entity tags
                int currentEtag = context.getEntityTag();
                int ifMatch = extractIfMatchValue(context);
                if (currentEtag != ifMatch) {
                    logger.error("Current entity tag '{}' doest match with the If-Match header '{}'.", currentEtag, ifMatch);
                    throw new LDPFault(HttpStatus.CONDITION_FAILED);
                } else {
                    currentEtag++;
                    logger.debug("Set the new etag to {} ...", currentEtag);
                }

                Dataset dataset = ConfigManager.getDataset();
                dataset.begin(ReadWrite.WRITE) ;
                try {
                    dataset.replaceNamedModel(ldprURI, model);
                    Model metaModel = dataset.getNamedModel(context.getProperty(LDPContext.META_URL));
                    Statement property = metaModel.getProperty(RdfUtils.resource(ldprURI), LDP4J.etag);
                    model.remove(property);
                    property.changeLiteralObject(currentEtag);
                    model.add(property);
                    dataset.commit();
                    logger.debug("Updated the resource '{}'", ldprURI);
                } finally {
                    dataset.end() ;
                }

            } else {
                String msg = String.format("Content type {} is not supported.", contentType);
                logger.debug(msg);
                throw new LDPFault(HttpStatus.UNSUPPORTED_MEDIA_TYPE, msg);
            }


        } else {
            throw new UnsupportedOperationException("Not implemented at the moment");
        }

        return HandlerResponse.CONTINUE;

    }

    @Override
    public String getName() {
        return NAME;
    }

    public int extractIfMatchValue(LDPContext context) throws LDPFault{

        HttpServletRequest request = context.getServletRequest();
        String etag = request.getHeader("If-Match");
        if (etag == null) {
            throw new LDPFault(HttpStatus.PRECONDITION_REQUIRED);
        } else {
            // Removing the surrounding quotation marks
            etag = etag.replace("\"", "");
            return Integer.valueOf(etag);
        }

    }
}
