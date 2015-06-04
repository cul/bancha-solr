package edu.columbia.ldpd.text.bancha.sax;

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class CollectionIdHandler extends DefaultHandler {
    private String currentCollection = null;
    private final Hashtable<String, String> idToCollectionHash;

    public CollectionIdHandler(String currentCollection, Hashtable<String,String> idToCollectionHash) {
        super();
        this.idToCollectionHash = idToCollectionHash;
        setCollectionId(currentCollection);
    }

    public Hashtable<String,String> idToCollectionHash() {
        return this.idToCollectionHash;
    }

    public void setCollectionId(String currentCollection) {
        this.currentCollection = currentCollection;
    }
    /**
     * @param sName simple name (localName)
     * @param qName qualified name
     * @param attra
     * @throws org.xml.sax.SAXException
     */
    public void startElement(String namespaceURI,
            String sName,
            String qName,
            Attributes attrs) throws SAXException {
        // if (sName != null && ! sName.equals("")) System.out.println("sName=" + sName);
        // if (qName != null && ! qName.equals("")) System.out.println("qName=" + qName);
        if (qName.equals("mets:div") && "Collection".equals(attrs.getValue("TYPE"))){
        	if (currentCollection == null) {
        		String currentCollection = attrs.getValue("LABEL");
            	if ("Corporate Reports Collection".equals(currentCollection)) {
            		currentCollection = "corprep";
            	}
            	if ("Library Columns Collection".equals(currentCollection)) {
            		currentCollection = "columns";
            	}
            	if ("Digital New York City Books".equals(currentCollection)) {
            		currentCollection = "dnyc";
            	}
            	this.currentCollection = currentCollection;
        	}
        }
        if (qName.equals("mets:mptr") ) {
            String xlink = attrs.getValue("xlink:href");
            if (xlink == null || xlink.equals("")) return;

            // System.out.println("xlink=" + xlink);
            String basename = xlink.replaceAll("_mets.xml", "");
            //System.out.println("basename=" + basename);

            String currentHashValue = (String)idToCollectionHash.get(basename);
            if ( currentHashValue == null) {
                idToCollectionHash.put(basename, currentCollection);
            } else {
                idToCollectionHash.put(basename, currentHashValue + " " + currentCollection);
            } 
        }

    }

    // SAX handler method for character data within XML tags
    public void characters(char buf[], int offset, int len) 
            throws SAXException
            {
    }

    @Override
    public void endElement(String namespaceURI,
            String sName,
            String qName) throws SAXException {
    }
}
