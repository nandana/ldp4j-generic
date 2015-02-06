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

import org.ldp4j.generic.http.MetadataMap;
import org.ldp4j.generic.http.MultivaluedMap;
import org.ldp4j.generic.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Utility class for decoding and encoding URLs from Apache CXF source code.
 * Original class org.apache.cxf.common.util.UrlUtils
 *
 */
public final class UrlUtils {

    private static final int RADIX = 16;
    private static final byte ESCAPE_CHAR = '%';
    private static final byte PLUS_CHAR = '+';

    private UrlUtils() {

    }

    public static String urlEncode(String value) {

        return urlEncode(value, "UTF-8");
    }

    public static String urlEncode(String value, String enc) {

        try {
            value = URLEncoder.encode(value, enc);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        return value;
    }

    /**
     * Decodes using URLDecoder - use when queries or form post values are decoded
     * @param value value to decode
     * @param enc encoding
     */
    public static String urlDecode(String value, String enc) {
        return urlDecode(value, enc, false);
    }

    private static String urlDecode(String value, String enc, boolean isPath) {

        boolean needDecode = false;
        int escapesCount = 0;
        int i = 0;
        while (i < value.length()) {
            char ch = value.charAt(i++);
            if (ch == ESCAPE_CHAR) {
                escapesCount += 1;
                i += 2;
                needDecode = true;
            } else if (!isPath && ch == PLUS_CHAR) {
                needDecode = true;
            }
        }
        if (needDecode) {
            final byte[] valueBytes = StringUtils.toBytes(value, enc);
            ByteBuffer in = ByteBuffer.wrap(valueBytes);
            ByteBuffer out = ByteBuffer.allocate(in.capacity() - 2 * escapesCount);
            while (in.hasRemaining()) {
                final int b = in.get();
                if (!isPath && b == PLUS_CHAR) {
                    out.put((byte) ' ');
                } else if (b == ESCAPE_CHAR) {
                    try {
                        final int u = digit16((byte) in.get());
                        final int l = digit16((byte) in.get());
                        out.put((byte) ((u << 4) + l));
                    } catch (final ArrayIndexOutOfBoundsException e) {
                        throw new RuntimeException("Invalid URL encoding: ", e);
                    }
                } else {
                    out.put((byte) b);
                }
            }
            out.flip();
            return Charset.forName(enc).decode(out).toString();
        } else {
            return value;
        }
    }

    private static int digit16(final byte b) {
        final int i = Character.digit((char) b, RADIX);
        if (i == -1) {
            throw new RuntimeException("Invalid URL encoding: not a valid digit (radix " + RADIX + "): " + b);
        }
        return i;
    }


    public static String urlDecode(String value) {
        return urlDecode(value, "UTF-8");
    }

    /**
     * URL path segments may contain '+' symbols which should not be decoded into ' '
     * This method replaces '+' with %2B and delegates to URLDecoder
     * @param value value to decode
     */
    public static String pathDecode(String value) {
        return urlDecode(value, "UTF-8", true);
    }


    /**
     * Create a map from String to String that represents the contents of the query
     * portion of a URL. For each x=y, x is the key and y is the value.
     * @param s the query part of the URI.
     * @return the map.
     */
    public static Map<String, String> parseQueryString(String s) {
        Map<String, String> ht = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                ht.put(pair.toLowerCase(), "");
            } else {
                ht.put(pair.substring(0, pos).toLowerCase(),
                        pair.substring(pos + 1));
            }
        }
        return ht;
    }

    /**
     * Return everything in the path up to the last slash in a URI.
     * @param baseURI
     * @return the trailing
     */
    public static String getStem(String baseURI) {
        int idx = baseURI.lastIndexOf('/');
        String result = baseURI;
        if (idx != -1) {
            result = baseURI.substring(0, idx);
        }
        return result;
    }

    public static MultivaluedMap<String, String> getMatrixParams(String path, boolean decode) {
        int index = path.indexOf(';');
        return index == -1 ? new MetadataMap<String, String>()
                : getStructuredParams(path.substring(index + 1), ";", decode, false);
    }

    /**
     * Retrieve map of query parameters from the passed in message
     * @return a Map of query parameters.
     */
    public static MultivaluedMap<String, String> getStructuredParams(String query,
                                                                     String sep,
                                                                     boolean decode,
                                                                     boolean decodePlus) {
        MultivaluedMap<String, String> map =
                new MetadataMap<String, String>(new LinkedHashMap<String, List<String>>());

        getStructuredParams(map, query, sep, decode, decodePlus);

        return map;
    }

    public static void getStructuredParams(MultivaluedMap<String, String> queries,
                                           String query,
                                           String sep,
                                           boolean decode,
                                           boolean decodePlus) {
        if (!StringUtils.isEmpty(query)) {
            List<String> parts = Arrays.asList(StringUtils.split(query, sep));
            for (String part : parts) {
                int index = part.indexOf('=');
                String name = null;
                String value = null;
                if (index == -1) {
                    name = part;
                    value = "";
                } else {
                    name = part.substring(0, index);
                    value =  index < part.length() ? part.substring(index + 1) : "";
                    if (decodePlus && value.contains("+")) {
                        value = value.replace('+', ' ');
                    }
                    if (decode) {
                        value = (";".equals(sep))
                                ? HttpUtils.pathDecode(value) : HttpUtils.urlDecode(value);
                    }
                }
                queries.add(HttpUtils.urlDecode(name), value);
            }
        }
    }


}
