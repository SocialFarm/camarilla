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

    private ScoringFunction getScoringFunction( String resource ) throws IOException, ScriptException {
        final InputStream is = getClass().getClassLoader().getResourceAsStream( resource ) ;
        return new ScoringFunction( IOUtils.toString(is) ) ;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testTrivialScoringFunction() throws IOException, ScriptException, NoSuchMethodException {
        ScoringFunction scoringFunction = getScoringFunction( "TrivialScoringFunction.js") ;
        double score = scoringFunction.getMatchScore( " " , " " ) ;
        assertTrue ( 2.9 == score ) ;
    }

    // TODO : Add tests for
    // function "matchScore" not present in script should cause error
    // function returning non double or not castable to double should cause error
    // function exception eg on bad input data should be handled in reasonable manner
}
