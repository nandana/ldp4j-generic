/**
 * Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid (http://www.oeg-upm.net/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ldp4j.generic.ldp.runtime;

import com.hp.hpl.jena.rdf.model.*;
import org.ldp4j.generic.http.RepresentationPreference;
import org.ldp4j.generic.ldp.model.Preference;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.ldp4j.generic.util.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ldp4j.generic.util.RdfUtils.property;
import static org.ldp4j.generic.util.RdfUtils.resource;

public class DirectContainerStrategy extends BaseContainerStrategy {

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

    @Override
    public Model getPreferredRepresentation(String containerURI, Model model, RepresentationPreference preference) {

        boolean isMinital = preference.isMinimalInclusionRequired();

        Model preferredModel = null;

        // Remove the containment triples
        if ( isMinital || preference.isOmissiontRequired(Preference.CONTAINMENT_TRIPLES)) {
            StmtIterator containStatements = model.listStatements(model.createResource(containerURI), LDP.contains, (RDFNode) null);
            Model containmentTriples = ModelFactory.createDefaultModel();
            containmentTriples.add(containStatements);

            preferredModel = model.difference(containmentTriples);

        }
        //TODO finish the removal of membership triples

        return preferredModel;


    }


}
