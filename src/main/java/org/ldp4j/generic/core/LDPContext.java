package org.ldp4j.generic.core;

import com.hp.hpl.jena.rdf.model.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class LDPContext {

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private Model dataModel;

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

    public void setProperty(String key, String value){
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public static final String REQUEST_URL = "request_url";

    public static final String DATA_MODEL = "data_model";

}
