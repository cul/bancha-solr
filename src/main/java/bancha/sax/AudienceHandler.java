package bancha.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class AudienceHandler extends DefaultHandler {
    private String lastAudienceValue = "";
    private boolean audience = false;
    private StringBuffer buf = new StringBuffer();

    public AudienceHandler() {
        super();
    }

    @Override
    public void startDocument() {
        lastAudienceValue = "";
        audience = false;
        buf.setLength(0);
    }
    /**
     * @param sName simple name (localName)
     * @param qName qualified name
     * @param attra
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startElement(String namespaceURI,
            String sName,
            String qName,
            Attributes attrs) throws SAXException {
        this.audience = qName.equals("AUDIENCE");
    }

    // SAX handler method for character data within XML tags
    @Override
    public void characters(char buf[], int offset, int len) 
            throws SAXException
            {
        if (this.audience) this.buf.append(buf,offset,len);
    }

    @Override
    public void endElement(String namespaceURI,
            String sName,
            String qName) throws SAXException {
        if (this.audience) {
            String s = buf.toString();
            s = s.trim();
            if (!s.isEmpty()) lastAudienceValue = s;
        }
        this.audience = false;
        buf.setLength(0);
    }
    public String lastAudienceValue() {
        return this.lastAudienceValue;
    }
}
