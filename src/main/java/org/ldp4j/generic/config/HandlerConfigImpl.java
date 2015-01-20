package org.ldp4j.generic.config;

import org.ldp4j.generic.config.ConfigManager;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by nandana on 12/29/14.
 */
public class HandlerConfigImpl implements HandlerConfig {

    private Map<String, Object> properties = new Hashtable<String, Object>();

    @Override
    public void setProperty(String key, Object value) {
        properties.put(key,value);
    }

    @Override
    public Object getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public <T> T getProperty(String key, Class T) {
        try {
            Object obj =  properties.get(key);
            T value = (T) obj;
            return value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(String.format("{} can not be converted to type {}", key, T.getName()), e);
        }
    }
}
