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
package org.ldp4j.generic.core;

import org.ldp4j.generic.http.HttpStatus;

public class LDPFault extends Exception {

    // A flag that indicates whether it is possible to convert the fault into a meaningful HTTP error
    private boolean processable = false;

    // The corresponding HTTP status code for the fault
    private HttpStatus statusCode;

    public LDPFault(String msg){
        super(msg);
    }

    public LDPFault(String msg, Throwable e){
        super(msg, e);
    }

    public LDPFault(HttpStatus status) {
        statusCode = status;
        processable = true;

    }

    public LDPFault(HttpStatus status, String msg) {
        super(msg);
        statusCode = status;
        processable = true;
    }

    public LDPFault(HttpStatus status, String msg, Throwable e) {
        super(msg, e);
        statusCode = status;
        processable = true;
    }

    public boolean isProcessable(){
        return processable;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
