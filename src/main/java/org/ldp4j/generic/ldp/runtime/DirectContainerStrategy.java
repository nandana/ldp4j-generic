package org.ldp4j.generic.ldp.runtime;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import static org.ldp4j.generic.util.RdfUtils.resource;
import static org.ldp4j.generic.util.RdfUtils.property;

import org.ldp4j.generic.util.RdfUtils;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectContainerStrategy implements ContainerStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DirectContainerStrategy.class);

    protected Resource membershipResource;

    protected boolean isInverse;

    protected Resource hasMemberRelation;

    protected Resource isMemberOfRelation;

    @Override
    public void addMemberTriple(String containerURI, String newURI, Model resource, Model container) {

        logger.debug("Adding the new member '{}' to the  Direct container {}", newURI, containerURI);

        process(containerURI, container);

        if(isInverse){
            logger.debug("Adding the member triple ...\n <{}> <{}> <{}>", newURI, isMemberOfRelation.getURI(),
                    membershipResource.getURI());
            container.add(resource(newURI), property(isMemberOfRelation.getURI()), membershipResource);
        } else {
            logger.debug("Adding the member triple ...\n <{}> <{}> <{}>", membershipResource.getURI(), hasMemberRelation.getURI(),
                    newURI);
            container.add(membershipResource, property(hasMemberRelation.getURI()), resource(newURI));
        }

    }

    protected void process(String containerURI, Model model) {

        Resource container = model.getResource(containerURI);

        membershipResource = container.getPropertyResourceValue(LDP.membershipResource);
        hasMemberRelation = container.getPropertyResourceValue(LDP.hasMemberRelation);
        if(hasMemberRelation == null){
            isMemberOfRelation = container.getPropertyResourceValue(LDP.isMemberOfRelation);
            isInverse = true;
        }

        if (membershipResource == null || (hasMemberRelation == null && isMemberOfRelation == null)) {
            logger.error("Invalid direct container state ... \n{}", RdfUtils.toString(model));
            throw new IllegalStateException("Invalid direct container state");
        }

    }
}
