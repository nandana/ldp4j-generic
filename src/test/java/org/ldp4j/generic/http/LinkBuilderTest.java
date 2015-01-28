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
