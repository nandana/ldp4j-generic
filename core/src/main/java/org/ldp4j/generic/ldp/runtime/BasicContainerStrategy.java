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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.RepresentationPreference;
import org.ldp4j.generic.ldp.model.Preference;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicContainerStrategy extends BaseContainerStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BasicContainerStrategy.class);

    @Override
    public void addMemberTriple(String containerURI, String newURI, Model resource, Model container) {

        logger.debug("We don't have to do anything about membership triples in Basic Containers");
        return;
    }

    @Override
    public Model getPreferredRepresentation(String containerURI, Model model, RepresentationPreference preference) {

        // Remove the containment triples
        if (preference.isMinimalInclusionRequired() || preference.isOmissiontRequired(Preference.CONTAINMENT_TRIPLES)) {
            StmtIterator containStatements = model.listStatements(model.createResource(containerURI), LDP.contains, (RDFNode) null);
            Model containmentTriples = ModelFactory.createDefaultModel();
            containmentTriples.add(containStatements);

            return model.difference(containmentTriples);

        } else {
            Model copy = ModelFactory.createDefaultModel();
            return copy.add(model);
        }

    }

}
