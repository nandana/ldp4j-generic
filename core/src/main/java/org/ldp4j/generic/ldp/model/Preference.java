package org.ldp4j.generic.ldp.model;

import org.ldp4j.generic.rdf.vocab.LDP;

public enum Preference {

    CONTAINMENT_TRIPLES(LDP.PREFER_CONTAINMENT),
    MEMBERSHIP_TRIPLES(LDP.PREFER_MEMBERSHIP),
    MINIMAL_CONTAINER(LDP.PREFER_MINIMAL_CONTAINER),
    EMPTY_CONTAINER(LDP.PREFER_EMPTY_CONTAINER),
    ;

    private final String prefernce;

    private Preference(String prefernce) {
        this.prefernce = prefernce;
    }

    public static Preference fromString(String value) {
        for(Preference candidate:values()) {
            if(candidate.prefernce.equals(value)) {
                return candidate;
            }
        }
        return null;
    }




}
