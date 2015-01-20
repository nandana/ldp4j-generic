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
