package org.ldp4j.generic.http;

import org.junit.Test;
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
