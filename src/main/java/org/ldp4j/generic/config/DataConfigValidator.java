package org.ldp4j.generic.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import static org.ldp4j.generic.util.RdfUtils.resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DataConfigValidator {

    private static final Logger logger = LoggerFactory.getLogger(DataConfigValidator.class);

    private static final Set<String> INTERACTION_MODELS = ImmutableSet.of(
            "http://www.w3.org/ns/ldp#Resource", "http://www.w3.org/ns/ldp#BasicContainer",
            "http://www.w3.org/ns/ldp#DirectContainer", "http://www.w3.org/ns/ldp#IndirectContainer");

    public static boolean validate(String graphName, Model model) {

        Preconditions.checkNotNull(graphName, "The graphName can not be null");
        logger.debug("Validating the graph '{}'", graphName);

        Preconditions.checkNotNull(model, "The model to be validated can not be null");

        //check if it is a metadata graph or a data graph
        if (graphName.startsWith(ConfigManager.META_GRAPH_PREFIX)) {

            //Construct the associated resource name
            String resourceURI = graphName.replaceFirst(ConfigManager.META_GRAPH_PREFIX, ConfigManager.HTTP_PREFIX);

            // There is exactly one interaction type declared
            NodeIterator typeIterator = model.listObjectsOfProperty(resource(resourceURI), RDF.type);
            String type = null;
            if (typeIterator.hasNext()) {
                type = typeIterator.next().asResource().getURI();

                //Check if the type if valid
                if(!INTERACTION_MODELS.contains(type)) {
                    throw new IllegalArgumentException(String.format("Interaction model '%s' for resource '%s' is not valid",
                            type, resourceURI));
                }

                //check if there are more than one
                if(typeIterator.hasNext()){
                    String secondType = typeIterator.next().asResource().getURI();
                    throw new IllegalArgumentException(String.format("Multiple types were found for the resource %s: %s, %s",
                            resourceURI, type, secondType));
                }

            } else {
                throw new IllegalArgumentException(String.format("Data config validation error - %s doesn't have " +
                        "an interaction model defined.", graphName));
            }

            return true;


        } else {

            //Validate the data graphs
            //TODO validate

            return  false;


        }


    }

}
