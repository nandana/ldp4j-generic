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
package org.ldp4j.generic.util;

import org.apache.commons.lang3.StringUtils;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.http.UriBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {

    private static final Pattern ENCODE_PATTERN = Pattern.compile("%[0-9a-fA-F][0-9a-fA-F]");

    private static final String PATH_RESERVED_CHARACTERS = "=@/:!$&\'(),;~";

    private static final String QUERY_RESERVED_CHARACTERS = "?/,";

    public static String getHeaderString(List<String> values) {
        if (values == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                sb.append(value);
                if (i + 1 < values.size()) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
    }

    public static String getHeaderValue(String header, LDPContext context){

        HttpServletRequest request = context.getServletRequest();

        return request.getHeader(header);

    }

    public static Enumeration<String> getHeaderValues(String header, LDPContext context) {

        HttpServletRequest request = context.getServletRequest();

        return request.getHeaders(header);

    }

    /**
     * Encodes partially encoded string. Encode all values but those matching pattern
     * "percent char followed by two hexadecimal digits".
     *
     * @param encoded fully or partially encoded string.
     * @return fully encoded string
     */
    public static String encodePartiallyEncoded(String encoded, boolean query) {
        if (encoded.length() == 0) {
            return encoded;
        }
        Matcher m = ENCODE_PATTERN.matcher(encoded);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (m.find()) {
            String before = encoded.substring(i, m.start());
            sb.append(query ? HttpUtils.queryEncode(before) : HttpUtils.pathEncode(before));
            sb.append(m.group());
            i = m.end();
        }
        String tail = encoded.substring(i, encoded.length());
        sb.append(query ? HttpUtils.queryEncode(tail) : HttpUtils.pathEncode(tail));
        return sb.toString();
    }

    public static String queryEncode(String value) {

        return componentEncode(QUERY_RESERVED_CHARACTERS, value);
    }

    public static String urlEncode(String value) {

        return urlEncode(value, "UTF-8");
    }

    public static String urlEncode(String value, String enc) {

        return UrlUtils.urlEncode(value, enc);
    }

    public static String urlDecode(String value) {
        return UrlUtils.urlDecode(value);
    }


    public static String pathEncode(String value) {

        String result = componentEncode(PATH_RESERVED_CHARACTERS, value);
        // URLEncoder will encode '+' to %2B but will turn ' ' into '+'
        // We need to retain '+' and encode ' ' as %20
        if (result.indexOf('+') != -1) {
            result = result.replace("+", "%20");
        }
        if (result.indexOf("%2B") != -1) {
            result = result.replace("%2B", "+");
        }

        return result;
    }

    public static String pathDecode(String value) {
        return UrlUtils.pathDecode(value);
    }

    private static String componentEncode(String reservedChars, String value) {

        StringBuilder buffer = new StringBuilder();
        StringBuilder bufferToEncode = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);
            if (reservedChars.indexOf(currentChar) != -1) {
                if (bufferToEncode.length() > 0) {
                    buffer.append(urlEncode(bufferToEncode.toString()));
                    bufferToEncode.setLength(0);
                }
                buffer.append(currentChar);
            } else {
                bufferToEncode.append(currentChar);
            }
        }

        if (bufferToEncode.length() > 0) {
            buffer.append(urlEncode(bufferToEncode.toString()));
        }

        return buffer.toString();
    }

    public static URI relativize(URI base, URI uri) {
        // quick bail-out
        if (!(base.isAbsolute()) || !(uri.isAbsolute())) {
            return uri;
        }
        if (base.isOpaque() || uri.isOpaque()) {
            // Unlikely case of an URN which can't deal with
            // relative path, such as urn:isbn:0451450523
            return uri;
        }
        // Check for common root
        URI root = base.resolve("/");
        if (!(root.equals(uri.resolve("/")))) {
            // Different protocol/auth/host/port, return as is
            return uri;
        }

        // Ignore hostname bits for the following , but add "/" in the beginning
        // so that in worst case we'll still return "/fred" rather than
        // "http://example.com/fred".
        URI baseRel = URI.create("/").resolve(root.relativize(base));
        URI uriRel = URI.create("/").resolve(root.relativize(uri));

        // Is it same path?
        if (baseRel.getPath().equals(uriRel.getPath())) {
            return baseRel.relativize(uriRel);
        }

        // Direct siblings? (ie. in same folder)
        URI commonBase = baseRel.resolve("./");
        if (commonBase.equals(uriRel.resolve("./"))) {
            return commonBase.relativize(uriRel);
        }

        // No, then just keep climbing up until we find a common base.
        URI relative = URI.create("");
        while (!(uriRel.getPath().startsWith(commonBase.getPath())) && !(commonBase.getPath().equals("/"))) {
            commonBase = commonBase.resolve("../");
            relative = relative.resolve("../");
        }

        // Now we can use URI.relativize
        URI relToCommon = commonBase.relativize(uriRel);
        // and prepend the needed ../
        return relative.resolve(relToCommon);

    }

    public static URI resolve(UriBuilder baseBuilder, URI uri) {
        if (!uri.isAbsolute()) {
            return baseBuilder.build().resolve(uri);
        }
        return uri;
    }
}
