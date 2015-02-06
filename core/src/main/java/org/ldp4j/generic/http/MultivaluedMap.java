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

import java.util.List;
import java.util.Map;

public interface MultivaluedMap<K, V> extends Map<K, List<V>> {
    void putSingle(K var1, V var2);

    void add(K var1, V var2);

    V getFirst(K var1);

    void addAll(K var1, V... var2);

    void addAll(K var1, List<V> var2);

    void addFirst(K var1, V var2);

    boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> var1);
}
