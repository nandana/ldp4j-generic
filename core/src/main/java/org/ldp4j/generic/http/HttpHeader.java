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

/**
 * Created by nandana on 1/19/15.
 */
public enum HttpHeader {

    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ACCEPT_POST("Accept-Post"),
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

    private final String header;

    HttpHeader(String header){
        this.header = header;
    }

    public String header() {
        return header;
    }


}
