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

    final String name ;

    final String code ;

    public ScoringFunction( String functionName, String generatorCode ) throws CaramillaException {
        this.code = generatorCode ;
        this.name = functionName ;

        validate();

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        try {
            engine.eval(generatorCode);
        } catch (ScriptException e) {
            throw new CaramillaException( "script does not eval " ) ;
        }

        // confirm that the javascript engine implements the optional invocable interface
        if ( engine instanceof Invocable) {
            inv = (Invocable) engine;
        } else {
            throw new Error("scripting engine doesnt have Invocable support");
        }
    }


    public double getMatchScore(String a,  String b) throws CaramillaException {
        Object result = null;
        try {
            result = inv.invokeFunction( this.name , a, b );
        } catch (ScriptException e) {
            throw new CaramillaException( "script did not eval" ) ;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException( "unexpected loss of method after succesful init");
        }
        return (Double) result ;
    }


    private void validate() throws CaramillaException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            engine.eval(this.code);

            String functionType = (String) engine.eval( String.format( "typeof(%s)" , this.name) ) ;
            int numArgs = (Integer) engine.eval( String.format( "%s.length" , this.name)  ) ;
            if( !functionType.equals( "function" ) || numArgs != 2 ) {
                throw new CaramillaException( "code does not have a function taking two args with name " + name ) ;
            }
        } catch (ScriptException e) {
            throw new CaramillaException( "script does not eval" ) ;
        }
    }
}
