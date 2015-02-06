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

import org.ldp4j.generic.util.HttpUtils;
import org.ldp4j.generic.util.UrlUtils;

public class PathSegmentImpl implements PathSegment {

    private String path;

    public PathSegmentImpl(String path) {
        this(path, true);
    }

    public PathSegmentImpl(String path, boolean decode) {
        this.path = decode ? HttpUtils.pathDecode(path) : path;
    }

    public MultivaluedMap<String, String> getMatrixParameters() {
        return UrlUtils.getMatrixParams(path, false);
    }

    public String getPath() {
        int index = path.indexOf(';');
        String value = index != -1 ? path.substring(0, index) : path;
        if (value.startsWith("/")) {
            value = value.length() == 1 ? "" : value.substring(1);
        }
        return value;
    }

    public String getOriginalPath() {
        return path;
    }

    public String getMatrixString() {
        int index = path.indexOf(';');
        if (index == -1) {
            return null;
        } else {
            return path.substring(index);
        }
    }

    public String toString() {
        return path;
    }

}
