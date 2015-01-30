package org.ldp4j.generic.ldp.runtime;

import com.hp.hpl.jena.rdf.model.Model;

public class IndirectContainerStrategy extends DirectContainerStrategy {
    @Override
    public void addMemberTriple(String containerURI, String newURI, Model resource, Model container) {

    }

    @Override
    protected void process(String containerURI, Model model) {
        super.process(containerURI, model);
    }
}
