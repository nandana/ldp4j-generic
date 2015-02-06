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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nandana on 1/19/15.
 */
public class MediaTypeTest {

    @Test
    public void testIsCompatible(){

        Map<String, String> params = new HashMap<String, String>();
        params.put("q", "0.5");
        MediaType textTurtleQ5 = new MediaType("text", "turtle", params);

        MediaType textTurtleQ10 = new MediaType("text", "turtle");

        boolean compatible = textTurtleQ10.isCompatible(textTurtleQ5);

        System.out.println(compatible);

    }
}
