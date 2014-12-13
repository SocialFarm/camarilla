package org.socialfarm.camarilla;



import javax.json.*;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by vpathak on 9/24/14.
 */
public class Order {

    static final String orderIdDiscriminator = UUID.randomUUID().toString() ;

    static long orderIdBase = 0 ;



    String      orderId ;

    String      itemName ;

    long        expiresAt;

    JsonObject  theOrder ;

    public boolean hasExpired() {
        return System.currentTimeMillis() > expiresAt ? true : false ;
    }

    public Order(String orderDetail) throws CamarillaException {
        try ( JsonReader jsonReader = Json.createReader(new StringReader(orderDetail)) ) {

            theOrder = jsonReader.readObject();

            JsonNumber expiresObject = theOrder.getJsonNumber( "expiresAt" );
            if( expiresObject != null )
                this.expiresAt = expiresObject.longValue();
            else
                throw new CamarillaException( "compulsory field expires at missing") ;

            JsonString itemNameString = theOrder.getJsonString("itemName") ;
            if( itemNameString != null )
                this.itemName = itemNameString.getString().trim() ;
            else
                throw new CamarillaException( "compulsory field item name missing" ) ;


            generateOrderId() ;
        }
    }

    // generate an order id for this order
    synchronized private void generateOrderId() {
        orderId = String.format( "%d-%s", orderIdBase++, orderIdDiscriminator );
    }
}

