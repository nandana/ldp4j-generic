package org.ldp4j.generic.config;

/**
 * Created by nandana on 12/31/14.
 */
public interface HandlerConfig {

    public void setProperty(String key, Object value);

    public Object getProperty(String key);

    public <T> T getProperty(String key, Class T);
}
