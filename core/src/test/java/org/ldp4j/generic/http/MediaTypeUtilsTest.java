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

import org.junit.Test;
import org.ldp4j.generic.util.MediaTypeUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nandana on 1/19/15.
 */
public class MediaTypeUtilsTest {

    @Test
    public void testParseMediaTypes() {

        List<MediaType> types = MediaTypeUtils.parseMediaTypes("text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c");

        for (MediaType mt : types) {
            System.out.println(mt.toString());
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("q", "0.5");
        MediaType textPlain = new MediaType("text", "plain", params);

        MediaType testHtml = new MediaType("text", "html");

        Map<String, String> dviParams = new HashMap<String, String>();
        params.put("q", "0.8");
        MediaType textXdvi = new MediaType("text", "x-dvi", params);

        MediaType textXc = new MediaType("text", "xc");

        assertThat(types, hasItems(textPlain));


    }
}
