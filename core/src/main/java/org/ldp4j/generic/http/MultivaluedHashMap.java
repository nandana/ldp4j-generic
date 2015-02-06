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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultivaluedHashMap<K, V> extends AbstractMultivaluedMap<K, V> implements Serializable {
    private static final long serialVersionUID = -6052320403766368902L;

    public MultivaluedHashMap() {
        super(new HashMap());
    }

    public MultivaluedHashMap(int initialCapacity) {
        super(new HashMap(initialCapacity));
    }

    public MultivaluedHashMap(int initialCapacity, float loadFactor) {
        super(new HashMap(initialCapacity, loadFactor));
    }

    public MultivaluedHashMap(MultivaluedMap<? extends K, ? extends V> map) {
        this();
        this.putAll(map);
    }

    private <T extends K, U extends V> void putAll(MultivaluedMap<T, U> map) {
        Iterator i$ = map.entrySet().iterator();

        while(i$.hasNext()) {
            Entry<T, U> e = (Entry<T, U>)i$.next();
            this.store.put(e.getKey(), new ArrayList((Collection)e.getValue()));
        }

    }

    public MultivaluedHashMap(Map<? extends K, ? extends V> map) {
        this();
        Iterator i$ = map.entrySet().iterator();

        while(i$.hasNext()) {
            Entry<K, V> e = (Entry<K, V>)i$.next();
            this.putSingle(e.getKey(), e.getValue());
        }

    }
}
