package org.ldp4j.generic.http;

public interface PathSegment {
    String getPath();

    MultivaluedMap<String, String> getMatrixParameters();
}
