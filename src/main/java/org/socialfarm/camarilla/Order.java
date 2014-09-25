package org.socialfarm.camarilla;

import javax.json.JsonObject;

/**
 * Created by vpathak on 9/24/14.
 */
public class Order {

    String      orderId ;

    String      itemName ;

    long        expireTimeStampMillis;

    JsonObject  theOrder ;

    public boolean hasExpired() {
        return System.currentTimeMillis() > expireTimeStampMillis ? true : false ;
    }
}

