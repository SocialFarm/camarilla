package org.socialfarm.camarilla;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.ws.rs.core.Application;


public class BasicServerTest extends JerseyTest
{
    @Override
    protected Application configure() {
        return new ResourceConfig( CamarillaEndPoints.class ) ;
    }

    @Test
    public void test() {
        final String status = target("status").request().get(String.class);
        assertEquals("{\"status\": \"OK\"}", status);
    }
}
