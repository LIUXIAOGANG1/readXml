package net.sourceforge.powerswing.util.date;

/**
 * Thrown when a class is expecting a Object (usually a String) is expected
 * in a particular format and the format is incorrect (i.e. it does not parse)
 *
 * @version 1.0
 * @author  Paul McClave
 */

public class BadFormatException extends Exception {
    /**
     * Constructs a BadFormatException with no detail message. 
     */
	public BadFormatException() {
	}
	
    /**
     * Constructs a BadFormatException with a supplied detailed message
     * 
     * @param s A Strign which is a detailed message
     */
	public BadFormatException(String s) {
	    super(s);
	}

    public BadFormatException(Throwable e) {
        super(e);
    }
}
