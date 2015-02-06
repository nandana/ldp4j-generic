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
