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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class LinkBuilderTest {

    @Test
    public void testParseLinkHeader() {

        String linkValue = "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"";
        List<Link> links = Link.parse(linkValue);

        assertThat(links.size(), is(1));

        Link link = links.get(0);

        String type = link.getRel();
        String value = link.getUri().toString();
        assertThat(type, is("type"));
        assertThat(value, is("http://www.w3.org/ns/ldp#Resource"));



    }

    public void testParseLinkHeaders(){

        String linkValue = "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\", " +
                "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"";
        List<Link> links = Link.parse(linkValue);

        assertThat(links.size(), is(2));

        Link link = links.get(0);

        link = Link.valueOf(linkValue);
        String type = link.getRel();
        String value = link.getUri().toString();
        assertThat(type, is("type"));
        assertThat(value, is("http://www.w3.org/ns/ldp#BasicContainer"));


        link = links.get(1);
        type = link.getRel();
        value = link.getUri().toString();
        assertThat(type, is("type"));
        assertThat(value, is("http://www.w3.org/ns/ldp#Resource"));

    }

}
