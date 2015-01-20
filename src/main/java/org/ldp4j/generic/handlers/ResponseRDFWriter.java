package org.ldp4j.generic.handlers;

import com.hp.hpl.jena.rdf.model.Model;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpStatus;
import org.ldp4j.generic.http.MediaType;
import org.ldp4j.generic.http.MediaTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        String accept = request.getHeader(HttpHeader.ACCEPT.value());
        logger.debug("Processing the accept header '{}'", accept);

        List<MediaType> mediaTypes = MediaTypeUtils.parseMediaTypes(accept);

        // Check for supported media types
        String format = null;
        for (MediaType mediaType: mediaTypes) {
            if (MediaType.TURTLE.isCompatible(mediaType)) {
                format = "TURTLE";
                break;
            } else if (MediaType.JSON_LD.isCompatible(mediaType)) {
                format = "JSON-LD";
                break;
            } else if (MediaType.RDF_XML.isCompatible(mediaType)) {
                format = "RDF/XML";
                break;
            }
        }

        if (format == null) {
            throw new LDPFault(HttpStatus.NOT_ACCEPTABLE, String.format("The accept header {} characteristics can not be " +
                        "fulfilled. Supported media types are : text/turtle, application/ld+json, and application/rdf+xml"));
        }

        HttpServletResponse response = context.getServletResponse();
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            dataModel.write(outputStream, format);
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
