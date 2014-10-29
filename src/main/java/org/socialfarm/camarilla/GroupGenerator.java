package org.socialfarm.camarilla;

import org.apache.commons.lang3.tuple.ImmutablePair;
import javax.script.ScriptException;
import java.util.*;


/**
 * generate groups based on the ranking implied by the javascript generator code
 * Created by vpathak on 9/24/14.
 */
class GroupGenerator {

    final ScoringFunction scoringFunction ;

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



    GroupGenerator( String functionName, String scoringCode ) throws CaramillaException {
        this.scoringFunction = new ScoringFunction(functionName, scoringCode) ;
    }


    private double getMatchScore(Order a,  Order b) throws CaramillaException {
        if ( a == null || a.hasExpired() || b == null || b.hasExpired() )
            return Double.NEGATIVE_INFINITY ;
        else
            return scoringFunction.getMatchScore( a.theOrder.toString() , b.theOrder.toString() ) ;
    }



    public String putOrder(Order order) throws CaramillaException  {
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
        return order.orderId ;
    }



    public Order getOrder(String orderId) {
        if( openOrders.containsKey( orderId ) && ! openOrders.get(orderId).hasExpired() ) {
            return openOrders.get(orderId) ;
        }
        return null ;
    }



    public List<Order> getMatchingOrders(String orderId, int maxOrders) {
        ArrayList<Order> matchingOrders = new ArrayList<Order>() ;
        if( ! openOrders.containsKey( orderId ) || openOrders.get(orderId).hasExpired() ) {
            return matchingOrders ;
        }
        double bestScore = bestMatch.get(orderId).getRight();
        int numOrders = 0;
        for( Map.Entry<Double, ImmutablePair<String,String>> entry : goodnessOrder.headMap( bestScore ).entrySet() ) {
            if( numOrders++ > maxOrders && entry.getValue().getLeft().equals( orderId ) ) {
                matchingOrders.add(openOrders.get(entry.getValue().getRight()));
            }
        }
        return matchingOrders ;
    }

}
