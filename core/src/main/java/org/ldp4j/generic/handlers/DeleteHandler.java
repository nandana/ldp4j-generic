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
import org.ldp4j.generic.rdf.vocab.LDP;
import org.ldp4j.generic.rdf.vocab.LDP4J;
import org.ldp4j.generic.util.LDP4JUtils;
import org.ldp4j.generic.util.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * <p>Delete handler provides the logic for resource deletion</p>
 * <ul>
 *     <li>Removes the resource named graph from the dataset</li>
 *     <li>Removes the metadata graph from the dataset</li>
 *     <li>If the given resource has a parent, removes the containment triple from the parent</li>
 * </ul>
 */
public class DeleteHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteHandler.class);

    private static final String NAME = "DeleteHandler";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        Dataset dataset = ConfigManager.getDataset();
        dataset.begin(ReadWrite.WRITE) ;
        try {
            String ldprURI = context.getProperty(LDPContext.REQUEST_URL);
            String metaURI = context.getProperty(LDPContext.META_URL);

            logger.trace("Deleting the resource {} ...", ldprURI);

            // Find the parent and remove the containment triple
            Model metaModel = dataset.getNamedModel(metaURI);
            NodeIterator iterator = metaModel.listObjectsOfProperty(RdfUtils.resource(ldprURI), LDP4J.parent);
            if (iterator.hasNext()) {
                Resource parent = iterator.next().asResource();
                Model parentModel = dataset.getNamedModel(parent.getURI());

                parentModel.remove(parent, LDP.contains, RdfUtils.resource(ldprURI));
                logger.debug("Removed the containment triple <{}, {}, {}>", parent.getURI(), LDP.contains.getURI(), ldprURI);

                if (iterator.hasNext()) {
                    String msg = "Multiple parent entries found for the resource.";
                    logger.error(msg);
                    throw new LDPFault(HttpStatus.INTERNAL_SERVER_ERROR, msg);
                }
            }

            if (LDP4JUtils.isContainer(context.getResourceType())) {
                deleteChildren(ldprURI, dataset);
            }

            dataset.removeNamedModel(ldprURI);
            dataset.removeNamedModel(metaURI);
            logger.debug("Removing the named graphs:\n data:{}\n metadata:{}", ldprURI, metaURI);
            dataset.commit();


        } finally {
            dataset.end() ;
        }

        return HandlerResponse.CONTINUE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void deleteChildren(String ldpc, Dataset dataset){

        Model container = dataset.getNamedModel(ldpc);
        NodeIterator iterator = container.listObjectsOfProperty(RdfUtils.resource(ldpc), LDP.contains);
        while (iterator.hasNext()) {

            Resource child = iterator.next().asResource();
            String childName = child.getURI();
            logger.debug("Deleting the child resource {} ...", childName);

            String childMetaName = LDP4JUtils.toMetadataURI(childName);
            Model model = dataset.getNamedModel(childMetaName);
            Resource type = model.getProperty(child, RDF.type).getObject().asResource();

            if (LDP4JUtils.isContainer(type)) {
                deleteChildren(childName, dataset);
            }

            dataset.removeNamedModel(childName);
            dataset.removeNamedModel(childMetaName);

        }


    }
}
