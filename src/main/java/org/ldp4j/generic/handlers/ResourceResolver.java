package org.ldp4j.generic.handlers;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import org.ldp4j.generic.config.ConfigManager;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

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
        logger.debug("Request URL : {}", url);

        String scheme = request.getScheme();
        String metaURL = getMetadataURI(url, scheme);
        logger.debug("Metadata URI : {}", metaURL);

        context.setProperty(LDPContext.REQUEST_URL, url);

        Dataset dataset = ConfigManager.getDataset();

        Model dataModel;
        Model metaModel;
        dataset.begin(ReadWrite.READ) ;
        try {
            dataModel = dataset.getNamedModel(url);
            metaModel = dataset.getNamedModel(metaURL);
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
