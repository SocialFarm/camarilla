package org.socialfarm.camarilla;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vpathak on 9/25/14.
 */
public class ScoringFunctionTest {

    /**
     * Read a function code from resource and initialize with the given function name
     * @param name
     * @param resource
     * @return
     * @throws IOException
     * @throws ScriptException
     */
    private ScoringFunction getScoringFunction( String name, String resource ) throws IOException, CamarillaException {
        final InputStream is = getClass().getClassLoader().getResourceAsStream( resource ) ;
        return new ScoringFunction( name, IOUtils.toString(is) ) ;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testTrivialScoringFunction() throws IOException, CamarillaException {
        ScoringFunction scoringFunction = getScoringFunction( "matchScore" , "TrivialScoringFunction.js") ;
        double score = scoringFunction.getMatchScore( " " , " " ) ;
        assertTrue ( 2.9 == score ) ;
    }


    // function "matchScore" is not present in script and should cause error
    @Test
    public void testInvalidScoringFunction() throws IOException {
        ScoringFunction scoringFunction = null;
        try {
            scoringFunction = getScoringFunction( "matchScore" , "InvalidScoringFunction.js" );
        } catch (CamarillaException e) {
            // expected error on invalid function name
            System.out.println( "got expected error :" + e.getLocalizedMessage() ) ;
            return ;
        }
        fail( "expected failure but scoring function was instantiated") ;
    }

    // TODO : Add tests for
    // function returning non double or not castable to double should cause error
    @Test
    public void testInvalidReturnValue() throws IOException, CamarillaException  {
        ScoringFunction scoringFunction = getScoringFunction( "matchScore" , "InvalidreturnScoringFunction.js" ) ;
        double score = scoringFunction.getMatchScore( " ", " ") ;
        assertFalse ( 2.9 == score ) ;
    }
    
    // function exception eg on bad input data should be handled in reasonable manner
    @Test
    public void testBadInputTestFunction() throws IOException {
        ScoringFunction scoringFunction = null ;
        try {
            scoringFunction = getScoringFunction ( "matchScore" , "InvalidFunctionTypeFunction.js") ;
        } catch (CamarillaException e) {
            //Expected Error
            System.out.println("got expected error " + e.getLocalizedMessage() );
            return ;
        }
        fail( "expected failure but scoring function was instantiated") ;
    }
}
