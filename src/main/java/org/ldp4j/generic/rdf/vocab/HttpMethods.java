package org.ldp4j.generic.rdf.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class HttpMethods {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();

    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2011/http-methods#";

    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}

    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );

    public static final Resource GET = m_model.createResource( NS + "GET" );

    public static final Resource PUT = m_model.createResource( NS + "PUT" );

    public static final Resource OPTIONS = m_model.createResource( NS + "OPTIONS" );

    public static final Resource HEAD = m_model.createResource( NS + "HEAD" );
}
