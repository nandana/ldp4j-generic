package org.ldp4j.generic.ldp.runtime;

import com.hp.hpl.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicContainerStrategy implements ContainerStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BasicContainerStrategy.class);

    @Override
    public void addMemberTriple(String containerURI, String newURI, Model resource, Model container) {

        logger.debug("We don't have to do anything about membership triples in Basic Containers");
        return;
    }
}
