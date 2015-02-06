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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class AbstractMultivaluedMap<K, V> implements MultivaluedMap<K, V> {
    protected final Map<K, List<V>> store;

    public AbstractMultivaluedMap(Map<K, List<V>> store) {
        if(store == null) {
            throw new NullPointerException("Underlying store must not be \'null\'.");
        } else {
            this.store = store;
        }
    }

    public final void putSingle(K key, V value) {
        List values = this.getValues(key);
        values.clear();
        if(value != null) {
            values.add(value);
        } else {
            this.addNull(values);
        }

    }

    protected void addNull(List<V> values) {
    }

    protected void addFirstNull(List<V> values) {
    }

    public final void add(K key, V value) {
        List values = this.getValues(key);
        if(value != null) {
            values.add(value);
        } else {
            this.addNull(values);
        }

    }

    public final void addAll(K key, V... newValues) {
        if(newValues == null) {
            throw new NullPointerException("Supplied array of values must not be null.");
        } else if(newValues.length != 0) {
            List values = this.getValues(key);
            Object[] arr$ = newValues;
            int len$ = newValues.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Object value = arr$[i$];
                if(value != null) {
                    values.add(value);
                } else {
                    this.addNull(values);
                }
            }

        }
    }

    public final void addAll(K key, List<V> valueList) {
        if(valueList == null) {
            throw new NullPointerException("Supplied list of values must not be null.");
        } else if(!valueList.isEmpty()) {
            List values = this.getValues(key);
            Iterator i$ = valueList.iterator();

            while(i$.hasNext()) {
                Object value = i$.next();
                if(value != null) {
                    values.add(value);
                } else {
                    this.addNull(values);
                }
            }

        }
    }

    public final V getFirst(K key) {
        List<V> values = this.store.get(key);
        return values != null && values.size() > 0?values.get(0):null;
    }

    public final void addFirst(K key, V value) {
        List values = this.getValues(key);
        if(value != null) {
            values.add(0, value);
        } else {
            this.addFirstNull(values);
        }

    }

    protected final List<V> getValues(K key) {
        List<V> l = (List<V>)this.store.get(key);
        if(l == null) {
            l = new LinkedList();
            this.store.put(key, l);
        }

        return (List)l;
    }

    public String toString() {
        return this.store.toString();
    }

    public int hashCode() {
        return this.store.hashCode();
    }

    public boolean equals(Object o) {
        return this.store.equals(o);
    }

    public Collection<List<V>> values() {
        return this.store.values();
    }

    public int size() {
        return this.store.size();
    }

    public List<V> remove(Object key) {
        return (List)this.store.remove(key);
    }

    public void putAll(Map<? extends K, ? extends List<V>> m) {
        this.store.putAll(m);
    }

    public List<V> put(K key, List<V> value) {
        return (List)this.store.put(key, value);
    }

    public Set<K> keySet() {
        return this.store.keySet();
    }

    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    public List<V> get(Object key) {
        return (List)this.store.get(key);
    }

    public Set<Entry<K, List<V>>> entrySet() {
        return this.store.entrySet();
    }

    public boolean containsValue(Object value) {
        return this.store.containsValue(value);
    }

    public boolean containsKey(Object key) {
        return this.store.containsKey(key);
    }

    public void clear() {
        this.store.clear();
    }

    public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> omap) {
        if(this == omap) {
            return true;
        } else if(!this.keySet().equals(omap.keySet())) {
            return false;
        } else {
            Iterator i$ = this.entrySet().iterator();

            while(i$.hasNext()) {
                Entry e = (Entry)i$.next();
                List olist = (List)omap.get(e.getKey());
                if(((List)e.getValue()).size() != olist.size()) {
                    return false;
                }

                Iterator i$1 = ((List)e.getValue()).iterator();

                while(i$1.hasNext()) {
                    Object v = i$1.next();
                    if(!olist.contains(v)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
