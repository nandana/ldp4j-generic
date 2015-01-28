package org.ldp4j.generic.ldp.runtime;

import com.hp.hpl.jena.rdf.model.Model;

public interface ContainerStrategy {

    public void addMemberTriple(String containerURI, String newURI, Model resource, Model container);
}
