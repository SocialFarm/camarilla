package org.socialfarm.camarilla;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static org.junit.Assert.*;

/**
 *
 * @author vpathak
 */
public class OrderTest {

    JsonObjectBuilder job ;

    JsonObject order ;

    public OrderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        job = Json.createObjectBuilder();
        order = job.add("testField", "TestValue").build();
        System.out.println( "created " + order.toString() ) ;
    }
    
    @After
    public void tearDown() {
    }

    //------------------------- test expiresAt ------------------------------------
    @Test
    public void testexpiresAtMustExist() {
        Order instance = null;
        try {
            instance = new Order(order.toString());
        } catch (CamarillaException e) {
            //e.printStackTrace();
            System.out.println( "expiredAt missing prevents order from being created - OK " ) ;
            return;
        }
        fail( "mus not create order if expiresAt is missing" );
    }


    @Test
    public void testexpiresAtWorks() throws InterruptedException {
        final long expiresAt = 2000 + System.currentTimeMillis() ;
        order = job.add("expiresAt" , expiresAt ).add( "itemName" , "testItem" ).build() ;

        Order instance = null;
        try {
            instance = new Order(order.toString());
        } catch (CamarillaException e) {
            e.printStackTrace();
            fail("must be able to create if expiredAt is present") ;
        }

        while( System.currentTimeMillis() < expiresAt ) {
            assertFalse("instance does not expire when it shouldnt ", instance.hasExpired());
            System.out.println( "tested hasExpired() works OK " ) ;
            Thread.sleep(1000);
        }

        assertTrue( "instance expires when it should " , instance.hasExpired() );
        System.out.println( "tested hasExpired() works OK " ) ;
    }


    //------------- item name tests --------------------------------
    @Test
    public void testitemNameMustExist() {
        final long expiresAt = 2000 + System.currentTimeMillis() ;
        order = job.add("expiresAt" , expiresAt ).build() ;

        Order instance = null;
        try {
            instance = new Order(order.toString());
        } catch (CamarillaException e) {
            //e.printStackTrace();
            System.out.println( "itemName missing prevents order from being created - OK " ) ;
            return;
        }
        fail( "mus not create order if itemName is missing" );
    }


}
