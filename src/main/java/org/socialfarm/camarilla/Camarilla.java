package org.socialfarm.camarilla;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vpathak on 9/30/14.
 */
public class Camarilla {

    private static final HashMap<String, String> scoringFunctions = new HashMap<String, String>() ;

    private static final HashMap<String, GroupGenerator> generators = new HashMap<String, GroupGenerator>() ;



    synchronized public static void addScoringFunction(String functionName, String jsCode) throws ScriptException {
        scoringFunctions.put( functionName , jsCode ) ;
    }



    synchronized public static String addOrder(String functionName,  String orderDetail) throws CaramillaException {
        try {

            GroupGenerator gg = generators.get(functionName);
            if (gg == null) {
                gg = new GroupGenerator(functionName, scoringFunctions.get(functionName));
                generators.put(functionName, gg);
            }

            return gg.putOrder( new Order(orderDetail) ) ;

        }
        catch(Exception ex) {
            return null ;
        }
    }



    synchronized public static List<Order> getMatchingOrders(String functionName, String orderId, int maxOrders ) throws CaramillaException {
        GroupGenerator gg = generators.get(functionName);
        if (gg == null) {
            throw new CaramillaException( "no function name " + functionName ) ;
        }
        return gg.getMatchingOrders( orderId , maxOrders ) ;
    }


}
