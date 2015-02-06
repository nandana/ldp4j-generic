package org.ldp4j.generic.util;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.rdf.vocab.LDP;

import java.util.Set;

public class LDP4JUtils {

    private static final Set<Resource> CONTAINERS = ImmutableSet.of(LDP.Container, LDP.BasicContainer,
            LDP.DirectContainer, LDP.IndirectContainer);

    private LDP4JUtils() {
        //a utility class
    }

    /**
     * Returns whether the given type is a container or not.
     * The container can be either Basic, Direct, or Indirect.
     * @param type
     * @return
     */
    public static boolean isContainer(Resource type){
        if (CONTAINERS.contains(type)){
            return true;
        } else {
            return false;
        }
    }

    /***
     * <p> Converts a resource URI to a metadata URI. A metadata URI in LDP4J has the scheme ldp4j://. </p>
     * @param resourceURI Resource URI associated with the metadata URI
     * @return metadata URI
     */
    public static String toMetadataURI(String resourceURI){

        return resourceURI.replaceFirst("^https?://", "ldp4j://");
    }

}
