package org.socialfarm.camarilla;


import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;


public class BasicServerTest extends JerseyTest
{
    @Override
    protected Application configure() {
        return new ResourceConfig( CamarillaEndPoints.class )
                .register(JsonProcessingFeature.class)
                .packages("org.glassfish.jersey.examples.jsonp")
                .property(JsonGenerator.PRETTY_PRINTING, true);
    }

    @Test
    public void testStatus() {
        final String status = target("status").request().get(String.class);
        assertEquals("{\"status\": \"OK\"}", status);
    }


    @Test
    public void testAddOrder() {
        JsonObject order = Json.createObjectBuilder()
                .add("test", "test").build() ;
        final Response response = target("addOrder/testfunction").request().post(Entity.json(order)) ;

        assertEquals(200, response.getStatus());

        JsonObject order2 = response.readEntity(JsonObject.class) ;

        System.out.println("got status 200 with data " + order2.toString() ) ;
    }
}
