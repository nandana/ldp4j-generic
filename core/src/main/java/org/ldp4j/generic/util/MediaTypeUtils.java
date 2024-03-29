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
import org.ldp4j.generic.http.MediaType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaTypeUtils {

    private static final Pattern COMPLEX_PARAMETERS =
            Pattern.compile("(([\\w-]+=\"[^\"]*\")|([\\w-]+=[\\w-/\\+]+))");

    public static List<MediaType> parseMediaTypes(String types) {
        List<MediaType> acceptValues = new ArrayList<MediaType>();

        if (types != null) {
            while (types.length() > 0) {
                String tp = types;
                int index = types.indexOf(',');
                if (index != -1) {
                    tp = types.substring(0, index);
                    types = types.substring(index + 1).trim();
                } else {
                    types = "";
                }
                acceptValues.add(toMediaType(tp));

                Collections.sort(acceptValues, new Comparator<MediaType>() {
                    @Override
                    public int compare(MediaType o1, MediaType o2) {

                        String q1Str = o1.getParameter("q");
                        String q2Str = o2.getParameter("q");

                        float q1, q2;
                        if (q1Str == null) {
                            q1 = 1;
                        } else {
                            q1 = Float.parseFloat(q1Str);
                        }

                        if (q2Str == null) {
                            q2 = 1;
                        } else {
                            q2 = Float.parseFloat(q2Str);
                        }

                        if (q1 == q2) {
                            return 0;
                        } else if (q1 > q2) {
                            return -1;
                        } else {
                            return +1;
                        }
                    }
                });



            }
        } else {
            acceptValues.add(MediaType.WILDCARD_TYPE);
        }

        return acceptValues;
    }

    public static MediaType toMediaType(String value) {
        if (value == null) {
            return MediaType.WILDCARD_TYPE;
        } else {
            return valueOf(value);
        }
    }

    public static MediaType valueOf(String mType) {

        if (mType == null) {
            throw new IllegalArgumentException("Media type value can not be null");
        }

        int i = mType.indexOf('/');
        if (i == -1) {
            return handleMediaTypeWithoutSubtype(mType.trim());
        }

        int paramsStart = mType.indexOf(';', i + 1);
        int end = paramsStart == -1  ? mType.length() : paramsStart;

        String type = mType.substring(0, i);
        String subtype = mType.substring(i + 1, end);

        Map<String, String> parameters = Collections.emptyMap();
        if (paramsStart != -1) {

            parameters = new LinkedHashMap<String, String>();

            String paramString = mType.substring(paramsStart + 1);
            if (paramString.contains("\"")) {
                Matcher m = COMPLEX_PARAMETERS.matcher(paramString);
                while (m.find()) {
                    String val = m.group().trim();
                    addParameter(parameters, val);
                }
            } else {
                StringTokenizer st = new StringTokenizer(paramString, ";");
                while (st.hasMoreTokens()) {
                    addParameter(parameters, st.nextToken());
                }
            }
        }

        return new MediaType(type.trim().toLowerCase(),
                subtype.trim().toLowerCase(),
                parameters);
    }

    private static MediaType handleMediaTypeWithoutSubtype(String mType) {

        if (mType.startsWith(MediaType.MEDIA_TYPE_WILDCARD)) {
            String mTypeNext = mType.length() == 1 ? "" : mType.substring(1).trim();
            boolean mTypeNextEmpty = StringUtils.isEmpty(mTypeNext);
            if (mTypeNextEmpty || mTypeNext.startsWith(";")) {
                if (!mTypeNextEmpty) {
                    Map<String, String> parameters = new LinkedHashMap<String, String>();
                    StringTokenizer st = new StringTokenizer(mType.substring(2).trim(), ";");
                    while (st.hasMoreTokens()) {
                        addParameter(parameters, st.nextToken());
                    }
                    return new MediaType(MediaType.MEDIA_TYPE_WILDCARD,
                            MediaType.MEDIA_TYPE_WILDCARD,
                            parameters);
                } else {
                    return MediaType.WILDCARD_TYPE;
                }

            }
        } else {
            throw new IllegalArgumentException("Media type separator is missing");
        }

        //TODO check why this is necessary
        return null;
    }

    private static void addParameter(Map<String, String> parameters, String token) {
        int equalSign = token.indexOf('=');
        if (equalSign == -1) {
            throw new IllegalArgumentException("Wrong media type parameter, separator is missing");
        }
        parameters.put(token.substring(0, equalSign).trim().toLowerCase(),
                token.substring(equalSign + 1).trim());
    }
}
