package net.sourceforge.powerswing.menu.history;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * This configuration class is used to store a list of previously opened files, 
 * it can be marshalled out to file for persistance between sessions using an
 * application.
 * 
 * @author mkerrigan
 */
public class PHistory {
    private String version;
    private ArrayList <String> files = new ArrayList <String> ();
    
    /**
     * Returns the files.
     * @return ArrayList
     */
    public ArrayList getFiles() {
        return files;
    }

    /**
     * Returns the version.
     * @return String
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the files.
     * @param files The files to set
     */
    public void setFiles(ArrayList <String> files) {
        this.files = files;
    }

    /**
     * Sets the version.
     * @param version The version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
    
    public void marshal(OutputStream os) throws IOException, MappingException, ValidationException, MarshalException {
        Mapping mapping = new Mapping();
        mapping.loadMapping(PHistory.class.getResource("phistorymapping.xml"));
        Marshaller marshaller = new Marshaller(new OutputStreamWriter(os, "UTF-8"));
        marshaller.setMapping(mapping);
        marshaller.marshal(this);
        os.close();
    }
    
    static public PHistory unmarshal(InputStream is) throws IOException, MappingException, ValidationException, MarshalException {
        Mapping mapping = new Mapping();
        mapping.loadMapping(PHistory.class.getResource("phistorymapping.xml"));
        Unmarshaller unmarshaller = new Unmarshaller(mapping);
        unmarshaller.setValidation(false);
        PHistory ap = (PHistory) unmarshaller.unmarshal(new InputStreamReader(is, "UTF-8"));
        is.close();
        return ap;
    }
    
    /*
     * BELOW HERE ARE HANDY METHODS FOR ADDING FILES BUT KEEPING THE LIST AT A MANGEABLE SIZE 
     */
     
    public void addFile(String theFile, int theHistoryListMaxSize){
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            String thisFile = (String) iter.next();
            if (thisFile.toLowerCase().equals(theFile.toLowerCase())){
                iter.remove();
            }
        }
        if (files.size() < theHistoryListMaxSize){
            files.add(0, theFile);
        }
        else{
            ArrayList <String> a = new ArrayList <String>();
            for (int i = 0; i < files.size(); i++){
                if (i < (theHistoryListMaxSize - 1)){
                    a.add(files.get(i));    
                }
            }
            a.add(0, theFile);
            files = a;
        }
    }
    
    public void removeFile(String theFile){
        files.remove(theFile);
    }
}