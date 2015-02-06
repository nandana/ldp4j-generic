package org.ldp4j.generic.ldp.runtime;

import com.hp.hpl.jena.rdf.model.*;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.rdf.vocab.LDP;

import java.util.Set;

public abstract class BaseContainerStrategy implements ContainerStrategy {

    public static boolean verifyContainmentTriples(String containerURI, Model oldModel, Model newModel) throws LDPFault {

        Resource container = oldModel.createResource(containerURI);

        StmtIterator iter = oldModel.listStatements(container, LDP.contains, (RDFNode) null);
        Set<Statement> oldContainmentTripls = iter.toSet();

        iter = newModel.listStatements(container, LDP.contains, (RDFNode) null);
        Set<Statement> newContainmentTriples = iter.toSet();

        boolean valid = true;

        if (oldContainmentTripls.size() == newContainmentTriples.size()) {
            for (Statement statement : oldContainmentTripls) {
                if (!newContainmentTriples.contains(statement)) {
                    valid = false;
                    break;
                }
            }
        } else {
            valid = false;
        }

        return valid;
    }
}
