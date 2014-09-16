package org.socialfarm.camarilla;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

/**
 * Endpoints supported by the restful service
 */

@Path(value="/")
public class CamarillaEndPoints {

    @GET
    @Path(value="/status")
    @Produces(value={"application/json"})
    String getStatus() {
        JSONObject response = new JSONObject() ;
        response.put( "status" , "OK" ) ;
        return response.toString( 4 ) ;
    }

}
