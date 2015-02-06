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
import org.ldp4j.generic.core.LDPFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicContainerStrategy extends BaseContainerStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BasicContainerStrategy.class);

    @Override
    public void addMemberTriple(String containerURI, String newURI, Model resource, Model container) {

        logger.debug("We don't have to do anything about membership triples in Basic Containers");
        return;
    }


}
