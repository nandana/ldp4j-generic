package org.ldp4j.generic.rdf.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class LDP {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();

    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/ns/ldp#";

    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}

    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );

    /** <p>Resource - A HTTP-addressable resource whose lifecycle is managed by a LDP server.</p> */
    public static final Resource Resource = m_model.createResource( NS + "Resource" );

    /** <p>RDFSource - A Linked Data Platform Resource (LDPR) whose state is represented as RDF.</p> */
    public static final Resource RDFSource = m_model.createResource( NS + "RDFSource" );

    /** <p>RDFSource - A Linked Data Platform Resource (LDPR) whose state is NOT represented as RDF.</p> */
    public static final Resource NonRDFSource = m_model.createResource( NS + "NonRDFSource" );

    /** <p>A Linked Data Platform RDF Source (LDP-RS) that also conforms to additional patterns and conventions for managing membership.
     * Readers should refer to the specification defining this ontology for the list of behaviors associated with it.</p> */
    public static final Resource Container = m_model.createResource( NS + "Container" );

    /** <p>RDFSource - An LDPC that uses a predefined predicate to simply link to its contained resources.</p> */
    public static final Resource BasicContainer = m_model.createResource( NS + "BasicContainer" );

    /** <p>DirectContainer - An LDPC that is similar to a LDP-DC but it allows an indirection with the ability to list as member a resource,
     * such as a URI representing a real-world object, that is different from the resource that is created</p> */
    public static final Resource DirectContainer = m_model.createResource( NS + "DirectContainer" );

    /** <p>IndirectContainer -  An LDPC that has the flexibility of choosing what form the membership triples take.</p> */
    public static final Resource IndirectContainer = m_model.createResource( NS + "IndirectContainer" );

    /** <p>contains - Links a container with resources created through the container.</p> */
    public static final Property contains = m_model.createProperty( NS + "contains" );


}