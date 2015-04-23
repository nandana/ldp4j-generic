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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.generic.http.HttpMethod;
import org.ldp4j.generic.http.RepresentationPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class LDPContext {

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private Model dataModel;

    private Resource resourceType;

    private HttpMethod method;

    private RepresentationPreference representationPreference;

    private int entityTag;

    private boolean putToCreate;

    public static final String REQUEST_URL = "request_url";

    public static final String META_URL = "meta_url";

    public static final String DATA_MODEL = "data_model";

    public static final String CREATED_RESOURCE_URI = "new_uri";

    public static final String METHOD = "method";

    public static final String RESP_CONTENT_TYPE = "response_content_type";

    public static final String INTERACTION_MODEL = "interaction_model";

    private Map<String, String> properties = new HashMap<String, String>();

    public LDPContext(){

    }

    public LDPContext(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }


    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    public Model getDataModel() {
        return dataModel;
    }

    public void setDataModel(Model dataModel) {
        this.dataModel = dataModel;
    }

    public Resource getResourceType() {
        return resourceType;
    }

    public void setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
    }

    public int getEntityTag() {
        return entityTag;
    }

    public void setEntityTag(int entityTag) {
        this.entityTag = entityTag;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setProperty(String key, String value){
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public boolean isPutToCreate() {
        return putToCreate;
    }

    public void setPutToCreate(boolean putToCreate) {
        this.putToCreate = putToCreate;
    }

    public RepresentationPreference getRepresentationPreference() {
        return representationPreference;
    }

    public void setRepresentationPreference(RepresentationPreference representationPreference) {
        this.representationPreference = representationPreference;
    }
}
