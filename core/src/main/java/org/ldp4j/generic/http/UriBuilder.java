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
package org.ldp4j.generic.http;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

public abstract class UriBuilder {
    protected UriBuilder() {
    }

    protected static UriBuilder newInstance() {
        return new UriBuilderImpl();
    }

    public static UriBuilder fromUri(URI uri) {
        return newInstance().uri((URI)uri);
    }

    public static UriBuilder fromUri(String uriTemplate) {
        return newInstance().uri((String)uriTemplate);
    }

    public static UriBuilder fromLink(Link link) {
        if(link == null) {
            throw new IllegalArgumentException("The provider \'link\' parameter value is \'null\'.");
        } else {
            return fromUri((URI)link.getUri());
        }
    }

    public static UriBuilder fromPath(String path) throws IllegalArgumentException {
        return newInstance().path((String)path);
    }

    public static UriBuilder fromResource(Class<?> resource) {
        return newInstance().path((Class)resource);
    }

    public static UriBuilder fromMethod(Class<?> resource, String method) {
        return newInstance().path(resource, method);
    }

    public abstract UriBuilder clone();

    public abstract UriBuilder uri(URI var1);

    public abstract UriBuilder uri(String var1);

    public abstract UriBuilder scheme(String var1);

    public abstract UriBuilder schemeSpecificPart(String var1);

    public abstract UriBuilder userInfo(String var1);

    public abstract UriBuilder host(String var1);

    public abstract UriBuilder port(int var1);

    public abstract UriBuilder replacePath(String var1);

    public abstract UriBuilder path(String var1);

    public abstract UriBuilder path(Class var1);

    public abstract UriBuilder path(Class var1, String var2);

    public abstract UriBuilder path(Method var1);

    public abstract UriBuilder segment(String... var1);

    public abstract UriBuilder replaceMatrix(String var1);

    public abstract UriBuilder matrixParam(String var1, Object... var2);

    public abstract UriBuilder replaceMatrixParam(String var1, Object... var2);

    public abstract UriBuilder replaceQuery(String var1);

    public abstract UriBuilder queryParam(String var1, Object... var2);

    public abstract UriBuilder replaceQueryParam(String var1, Object... var2);

    public abstract UriBuilder fragment(String var1);

    public abstract UriBuilder resolveTemplate(String var1, Object var2);

    public abstract UriBuilder resolveTemplate(String var1, Object var2, boolean var3);

    public abstract UriBuilder resolveTemplateFromEncoded(String var1, Object var2);

    public abstract UriBuilder resolveTemplates(Map<String, Object> var1);

    public abstract UriBuilder resolveTemplates(Map<String, Object> var1, boolean var2) throws IllegalArgumentException;

    public abstract UriBuilder resolveTemplatesFromEncoded(Map<String, Object> var1);

    public abstract URI buildFromMap(Map<String, ?> var1);

    public abstract URI buildFromMap(Map<String, ?> var1, boolean var2) throws IllegalArgumentException, UriBuilderException;

    public abstract URI buildFromEncodedMap(Map<String, ?> var1) throws IllegalArgumentException, UriBuilderException;

    public abstract URI build(Object... var1) throws IllegalArgumentException, UriBuilderException;

    public abstract URI build(Object[] var1, boolean var2) throws IllegalArgumentException, UriBuilderException;

    public abstract URI buildFromEncoded(Object... var1) throws IllegalArgumentException, UriBuilderException;

    public abstract String toTemplate();
}
