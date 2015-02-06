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

public enum HttpStatus {
    CONTINUE(100, "Continue"),
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406,"Not Acceptable"),
    CONFLICT(409, "Conflict"),
    CONDITION_FAILED(412, "Condition Failed"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type");

    private final int statusCode;

    private final String description;

    HttpStatus(int statusCode, String description){
        this.statusCode = statusCode;
        this.description = description;
    }

    public int code(){
        return  statusCode;
    }

    public String description(){
        return description;
    }
}
