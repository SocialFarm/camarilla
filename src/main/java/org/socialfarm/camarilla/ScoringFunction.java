package org.socialfarm.camarilla;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by vpathak on 9/25/14.
 */
public class ScoringFunction {

    final Invocable inv ;

    final String code ;

    public ScoringFunction( String generatorCode ) throws ScriptException {
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


    public double getMatchScore(String a,  String b) throws ScriptException, NoSuchMethodException {
        Object result = inv.invokeFunction( "matchScore" , a, b ) ;
        return (Double) result ;
    }
}
