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

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.*;

public abstract  class Link {

    public static final String TITLE = "title";
    public static final String REL = "rel";
    public static final String TYPE = "type";

    public Link() {
    }

    public abstract URI getUri();

    public abstract UriBuilder getUriBuilder();

    public abstract String getRel();

    public abstract List<String> getRels();

    public abstract String getTitle();

    public abstract String getType();

    public abstract Map<String, String> getParams();

    public abstract String toString();

    public static Link valueOf(String value) {
        Link.Builder b = new LinkBuilderImpl();
        b.link((String)value);
        return b.build(new Object[0]);
    }

    public static List<Link> parse(String value) {
        List<Link> links = new ArrayList<Link>();
        StringTokenizer st = new StringTokenizer(value, ",");
        while (st.hasMoreElements()) {
            Link link = valueOf(st.nextToken());
            links.add(link);
        }
        return links;
    }

    public static Link.Builder fromUri(URI uri) {
        Link.Builder b = new LinkBuilderImpl();
        b.uri((URI)uri);
        return b;
    }

    public static Link.Builder fromUri(String uri) {
        Link.Builder b = new LinkBuilderImpl();
        b.uri((String)uri);
        return b;
    }

    public static Link.Builder fromUriBuilder(UriBuilder uriBuilder) {
        Link.Builder b = new LinkBuilderImpl();
        b.uriBuilder(uriBuilder);
        return b;
    }

    public static Link.Builder fromLink(Link link) {
        Link.Builder b = new LinkBuilderImpl();
        b.link((Link)link);
        return b;
    }

    public static Link.Builder fromPath(String path) {
        return fromUriBuilder(UriBuilder.fromPath(path));
    }

    public static Link.Builder fromResource(Class<?> resource) {
        return fromUriBuilder(UriBuilder.fromResource(resource));
    }

    public static Link.Builder fromMethod(Class<?> resource, String method) {
        return fromUriBuilder(UriBuilder.fromMethod(resource, method));
    }

    public static class JaxbAdapter extends XmlAdapter<JaxbLink, Link> {
        public JaxbAdapter() {
        }

        public Link unmarshal(Link.JaxbLink v) {
            Link.Builder lb = Link.fromUri((URI)v.getUri());
            Iterator i$ = v.getParams().entrySet().iterator();

            while(i$.hasNext()) {
                Map.Entry e = (Map.Entry)i$.next();
                lb.param(((QName)e.getKey()).getLocalPart(), e.getValue().toString());
            }

            return lb.build(new Object[0]);
        }

        public Link.JaxbLink marshal(Link v) {
            Link.JaxbLink jl = new Link.JaxbLink(v.getUri());
            Iterator i$ = v.getParams().entrySet().iterator();

            while(i$.hasNext()) {
                Map.Entry e = (Map.Entry)i$.next();
                String name = (String)e.getKey();
                jl.getParams().put(new QName("", name), e.getValue());
            }

            return jl;
        }
    }

    public static class JaxbLink {
        private URI uri;
        private Map<QName, Object> params;

        public JaxbLink() {
        }

        public JaxbLink(URI uri) {
            this.uri = uri;
        }

        public JaxbLink(URI uri, Map<QName, Object> params) {
            this.uri = uri;
            this.params = params;
        }

        @XmlAttribute(
                name = "href"
        )
        public URI getUri() {
            return this.uri;
        }

        @XmlAnyAttribute
        public Map<QName, Object> getParams() {
            if(this.params == null) {
                this.params = new HashMap();
            }

            return this.params;
        }

        void setUri(URI uri) {
            this.uri = uri;
        }

        void setParams(Map<QName, Object> params) {
            this.params = params;
        }

        public boolean equals(Object o) {
            if(this == o) {
                return true;
            } else if(!(o instanceof Link.JaxbLink)) {
                return false;
            } else {
                Link.JaxbLink jaxbLink = (Link.JaxbLink)o;
                if(this.uri != null) {
                    if(this.uri.equals(jaxbLink.uri)) {
                        return this.params == jaxbLink.params?true:(this.params == null?jaxbLink.params.isEmpty():(jaxbLink.params == null?this.params.isEmpty():this.params.equals(jaxbLink.params)));
                    }
                } else if(jaxbLink.uri == null) {
                    return this.params == jaxbLink.params?true:(this.params == null?jaxbLink.params.isEmpty():(jaxbLink.params == null?this.params.isEmpty():this.params.equals(jaxbLink.params)));
                }

                return false;
            }
        }

        public int hashCode() {
            int result = this.uri != null?this.uri.hashCode():0;
            result = 31 * result + (this.params != null && !this.params.isEmpty()?this.params.hashCode():0);
            return result;
        }
    }

    public interface Builder {
        Link.Builder link(Link var1);

        Link.Builder link(String var1);

        Link.Builder uri(URI var1);

        Link.Builder uri(String var1);

        Link.Builder baseUri(URI var1);

        Link.Builder baseUri(String var1);

        Link.Builder uriBuilder(UriBuilder var1);

        Link.Builder rel(String var1);

        Link.Builder title(String var1);

        Link.Builder type(String var1);

        Link.Builder param(String var1, String var2);

        Link build(Object... var1);

        Link buildRelativized(URI var1, Object... var2);
    }
}
