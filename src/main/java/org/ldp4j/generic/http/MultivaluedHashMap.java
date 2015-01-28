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
