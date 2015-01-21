package org.ldp4j.generic.http;

import java.util.*;

public class MediaType {

    private String type;

    private String subtype;

    private Map<String, String> parameters;

    public static final String CHARSET_PARAMETER = "charset";

    public static final String MEDIA_TYPE_WILDCARD = "*";

    public static final String WILDCARD = "*/*";

    public static final MediaType WILDCARD_TYPE = new MediaType();

    public static final MediaType TURTLE = new MediaType("text", "turtle");

    public static final MediaType JSON_LD = new MediaType("application", "ld+json");

    public static final MediaType RDF_XML = new MediaType("application", "rdf+xml");

    public static final String APPLICATION_XML = "application/xml";



    private MediaType(String type, String subtype, String charset, Map<String, String> parameterMap) {
        this.type = type == null?"*":type;
        this.subtype = subtype == null?"*":subtype;
        if(parameterMap == null) {
            parameterMap = new TreeMap(new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
        }

        if(charset != null && !charset.isEmpty()) {
            ((Map)parameterMap).put("charset", charset);
        }

        this.parameters = Collections.unmodifiableMap((Map) parameterMap);
    }

    public MediaType(String type, String subtype, String charset) {
        this(type, subtype, charset, (Map)null);
    }

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this(type, subtype, (String)null, createParametersMap(parameters));
    }

    public MediaType(String type, String subtype) {
        this(type, subtype, (String)null, (Map)null);
    }

    public MediaType() {
        this("*", "*", (String)null, (Map)null);
    }

    public String getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public String getParameter(String key){
        if(parameters != null) {
            return parameters.get(key);
        } else {
            return null;
        }
    }

    public boolean isWildcardType() {
        return this.getType().equals("*");
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof MediaType)) {
            return false;
        } else {
            MediaType other = (MediaType)obj;
            return this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype) && this.parameters.equals(other.parameters);
        }
    }

    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + this.parameters.hashCode();
    }

    public boolean isCompatible(MediaType other) {
        return other != null && (this.type.equals("*") || other.type.equals("*") || this.type.equalsIgnoreCase(other.type) && (this.subtype.equals("*") || other.subtype.equals("*")) || this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype));
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("Type: ");
        sb.append(this.type);
        sb.append(", Subtype: ");
        sb.append(this.subtype);
        if (parameters != null && parameters.size() > 0) {
            sb.append(", Parameters: ");
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                sb.append("[" + param.getKey() + " = " + param.getValue() + "]");
            }
        }

        return sb.toString();
    }

    public String getValue(){
        if (subtype != null){
            return String.format("%s/%s", type, subtype);
        } else {
            return type;
        }
    }

    private static TreeMap<String, String> createParametersMap(Map<String, String> initialValues) {
        TreeMap map = new TreeMap(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        if(initialValues != null) {
            Iterator i$ = initialValues.entrySet().iterator();

            while(i$.hasNext()) {
                Map.Entry e = (Map.Entry)i$.next();
                map.put(((String)e.getKey()).toLowerCase(), e.getValue());
            }
        }

        return map;
    }






}
