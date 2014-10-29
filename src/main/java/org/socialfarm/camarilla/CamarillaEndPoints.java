package org.socialfarm.camarilla;

import javax.script.ScriptException;
import javax.ws.rs.*;

import org.json.JSONObject;

/**
 * Endpoints supported by the restful service
 */

@Path(value="/")
public class CamarillaEndPoints {

    private static final Camarilla camarilla = new Camarilla() ;

    private String getStatusJSON( String status ) { return getStatusJSON(status, null) ; }

    private String getStatusJSON( String status, String detail ) {
        JSONObject response = new JSONObject() ;
        response.put( "status" , status ) ;
        if( detail != null ) {
            response.put( "detail" , detail ) ;
        }
        return response.toString( 4 ) ;
    }


    @GET
    @Path(value="/status")
    @Produces(value={"application/json"})
    public String getStatus() {
        return getStatusJSON("OK") ;
    }


    @POST
    @Path(value="/addRankingFunction/{functionName}")
    @Consumes(value = {"application/json"})
    @Produces(value = {"application/json"})
    public String addRankingFunction(@PathParam(value="functionName") String functionName,  String functionCode) {
        try {
            camarilla.addScoringFunction(functionName, functionCode);
            return getStatusJSON("OK");
        }
        catch (ScriptException ex) {
            ex.printStackTrace();
            return getStatusJSON("ERROR" , ex.getMessage() ) ;
        }
    }


    @POST
    @Path(value="/addOrder/{functionName}")
    @Consumes(value = {"application/json"})
    @Produces(value = {"application/json"})
    public String addOrder(@PathParam(value="functionName") String functionName,  String orderDetail) {
        try {
            String orderId = camarilla.addOrder(functionName, orderDetail);
            JSONObject response = new JSONObject();
            response.put("orderId", orderId);
            return response.toString(4);
        }
        catch (CamarillaException ex) {
            ex.printStackTrace();
            return getStatusJSON("ERROR" , ex.getMessage() ) ;
        }
    }


    @GET
    @Path(value="/matchRank/{functionName}/{orderId}")
    @Produces(value = {"application/json"})
    public String getMatchingOrders(@PathParam(value="functionName") String functionName,
                                    @PathParam(value="orderId") String orderId,
                                    @QueryParam("maxOrders") int maxOrders)
    {
        try {
            camarilla.getMatchingOrders(functionName, orderId, maxOrders) ;
        } catch (CamarillaException ex) {
            ex.printStackTrace();
            return getStatusJSON("ERROR" , ex.getMessage() ) ;
        }
        return "{ [ list of orders ] }" ;
    }

}
