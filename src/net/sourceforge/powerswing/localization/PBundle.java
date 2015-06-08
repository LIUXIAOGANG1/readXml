package net.sourceforge.powerswing.localization;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is the super class for all the resource bundles for each of the applications
 * so that a resource bundle from any of the applications can be based into the shared
 * application util classes.
 * 
 * @author mkerrigan
 */
public interface PBundle {
    public abstract String getString(String theKey);
    public abstract String getStringWithNoErrorHandling(String theKey) throws MissingResourceException;
    public abstract char getChar(String theKey);
    public abstract int getInt(String theKey);
    public abstract String format(String theKey, Object[] theValues);
	public abstract String formatWithNoErrorHandling(String theKey, Object[] theValues) throws MissingResourceException;
    public abstract ResourceBundle getBundle();
    public abstract MessageFormat getFormatter();
    public abstract Locale getLocale();
    public abstract String getLanguage();
    public abstract StringAndMnemonic getStringAndMnemonic(String theKey);
}