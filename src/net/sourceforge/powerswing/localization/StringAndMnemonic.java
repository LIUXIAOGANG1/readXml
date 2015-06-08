package net.sourceforge.powerswing.localization;

public class StringAndMnemonic {

    private String string;
    private char mneumonic;
    private int mneumonicIndex;

    public StringAndMnemonic(String theString, char theMnemonic, int theMnemonicIndex) {
        this.string = theString;
        this.mneumonic = theMnemonic;
        this.mneumonicIndex = theMnemonicIndex;
    }

    public char getMneumonic() {
        return mneumonic;
    }

    public int getMneumonicIndex() {
        return mneumonicIndex;
    }

    public String getString() {
        return string;
    }

    public String toString() {
        return string + " " + mneumonic + " " + mneumonicIndex;
    }

}