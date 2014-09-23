package org.socialfarm.camarilla;

import javax.ws.rs.*;

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


    @POST
    @Path(value="/order/{itemName}")
    @Consumes(value = {"application/json"})
    @Produces(value = {"application/json"})
    public String addOrder(@PathParam(value="itemName") String itemName,  String orderDetail) {
        return "{ orderid = 111 }" ;
    }


    @GET
    @Path(value="/matchRank/{orderId}")
    @Produces(value = {"application/json"})
    public String addOrder(@PathParam(value="orderId") String itemName) {
        return "{ [ list of orders ] }" ;
    }

}
