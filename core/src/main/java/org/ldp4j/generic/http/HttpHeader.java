package org.ldp4j.generic.http;

/**
 * Created by nandana on 1/19/15.
 */
public enum HttpHeader {

    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ALLOW("Allow"),
    AUTHORIZATION("Authorization"),
    CACHE_CONTROL("Cache-Control"),
    CONTENT_DISPOSITION("Content-Disposition"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_ID("Content-ID"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_LOCATION("Content-Location"),
    CONTENT_TYPE("Content-Type"),
    DATE("Date"),
    ETAG("ETag"),
    EXPIRES("Expires"),
    HOST("Host"),
    IF_MATCH("If-Match"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    IF_NONE_MATCH("If-None-Match"),
    IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
    LAST_MODIFIED("Last-Modified"),
    LOCATION("Location"),
    LINK("Link"),
    RETRY_AFTER("Retry-After"),
    USER_AGENT("User-Agent"),
    VARY("Vary"),
    WWW_AUTHENTICATE("WWW-Authenticate"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),
    ACCESS_CONTROL_EXPOSE_HEADERS("Access-Control-Expose-Headers");

    private final String name;

    HttpHeader(String name){
        this.name = name;
    }

    public String value(){
        return name;
    }



}
