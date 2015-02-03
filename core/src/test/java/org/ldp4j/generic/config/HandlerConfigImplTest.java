package org.ldp4j.generic.config;

import org.junit.Test;

import java.util.Date;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class HandlerConfigImplTest {

    @Test
    public void testGetPropertyAsClass(){

        HandlerConfig config = new HandlerConfigImpl();
        config.setProperty("proA", new Date());

        Date propA = config.getProperty("proA", Date.class);
        assertThat("Returned ", propA, is(instanceOf(Date.class)));

    }
}
