package org.socialfarm.camarilla;

/**
 * Created by vpathak on 9/25/14.
 */
public class CamarillaException extends Exception {
    public CamarillaException(String s) {
        super(s);
    }

    public CamarillaException(String s, Exception ex) {
        super(s, ex);
    }
}
