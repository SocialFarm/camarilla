package org.socialfarm.camarilla;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * generate groups based on the ranking implied by the javascript generator code
 * Created by vpathak on 9/24/14.
 */
class GroupGenerator {

    final Invocable inv ;

    final String code ;

    /**
     * map of orderids to orders. this data structure is the "correct" set of orders
     * every cleanup must first clean the reference here.  insertions must insert here last
     */
    final HashMap<String, Order> openOrders = new HashMap<String, Order>();

    /**
     * ordering of open orders by expiry time.  this ordering is maintained in order to quickly
     */
    final TreeMap<Long, Order> expiryOrder = new TreeMap<Long, Order>() ;

    /**
     * Maintain a goodness order with records like : <Goodness value, Order a, Order b> such
     * that the best match can be greedily determined quickly.  This order is partially correct
     * in the sense that its ordering is correct for those orders which are open,  however
     * it may contain references to expired orders which must be garbage collected at a reasonable time
     */
    final TreeMap<Double, ImmutablePair<String,String>> goodnessOrder = new TreeMap<Double, ImmutablePair<String, String>>() ;

    /**
     * best match for order id
     */
    final HashMap<String, ImmutablePair<String, Double> > bestMatch = new HashMap<String, ImmutablePair<String, Double> >() ;



    GroupGenerator( String generatorCode ) throws ScriptException {
        this.code = generatorCode ;

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(generatorCode);

        // confirm that the javascript engine implements the optional invocable interface
        if ( engine instanceof Invocable) {
            inv = (Invocable) engine;
        } else {
            throw new Error("scripting engine doesnt have Invocable support");
        }
    }


    private double getMatchScore(Order a,  Order b) throws ScriptException, NoSuchMethodException {
        if ( a == null || a.hasExpired() || b == null || b.hasExpired() )
            return Double.NEGATIVE_INFINITY ;
        Object result = inv.invokeFunction( "matchScore" ,
                a.theOrder.toString() , b.theOrder.toString() ) ;
        return (Double) result ;
    }



    public void putOrder(Order order) throws CaramillaException, ScriptException, NoSuchMethodException {
        // can not reinsert order unless expired
        if( openOrders.containsKey( order.orderId ) &&
                ! openOrders.get(order.orderId).hasExpired() ) {
            throw new CaramillaException( "order " + order + " was already in set of open unexpired orders ") ;
        }

        // add into the expiry order first so it will get removed eventually regardless
        expiryOrder.put( order.expireTimeStampMillis , order ) ;

        double best = Double.NEGATIVE_INFINITY ;
        for( Order o : openOrders.values() ) {
            if( o.hasExpired() )
                continue;

            double score = getMatchScore(o, order) ;

            // maintain goodnessOrder
            goodnessOrder.put(score, ImmutablePair.of(o.orderId, order.orderId)) ;

            goodnessOrder.put(score, ImmutablePair.of(order.orderId, o.orderId)) ;

            // maintain best for the new order just added
            if( score > best ) {
                best = score ;
                bestMatch.put( order.orderId , ImmutablePair.of( o.orderId, score ) );
            }

            // maintain best for others (does adding this order change the best for another)
            double oldOtherBestScore = getMatchScore( getOrder(bestMatch.get(o.orderId).getLeft()) , order ) ;
            if( score >  oldOtherBestScore ) {
                bestMatch.put(o.orderId , ImmutablePair.of(order.orderId, score) ) ;
            }
        }

        // lastly put the order in authoritative list
        openOrders.put( order.orderId , order ) ;
    }

    public Order getOrder(String orderId) {
        if( openOrders.containsKey( orderId ) && ! openOrders.get(orderId).hasExpired() ) {
            return openOrders.get(orderId) ;
        }
        return null ;
    }

}
