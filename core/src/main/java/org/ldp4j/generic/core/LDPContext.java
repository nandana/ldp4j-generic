package org.ldp4j.generic.core;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class LDPContext {

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private Model dataModel;

    private Resource resourceType;

    private int entityTag;

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

    public void setProperty(String key, String value){
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public static final String REQUEST_URL = "request_url";

    public static final String META_URL = "meta_url";

    public static final String DATA_MODEL = "data_model";

    public static final String CREATED_RESOURCE_URI = "new_uri";

    public static final String METHOD = "method";

    public static final String RESP_CONTENT_TYPE = "response_content_type";

    public static final String INTERACTION_MODEL = "interaction_model";

}
