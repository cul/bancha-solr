package bancha;
import java.io.File;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;


public class GetCollectionBibs 
  extends DefaultHandler {

  public static void main(String argv[]) {

    if (argv.length != 1) {
      System.err.println("Usage: java GetCollectionBibs collection_mets.xml");
      System.exit(1);
    }

    // Use an instance of ourselves as the SAX event handler
    DefaultHandler handler = new GetCollectionBibs();

    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();

    try {

      // Parse the input
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse( new File(argv[0]), handler );

    } catch (Throwable t) {
      t.printStackTrace();
    }

  }

  public void startElement(String namespaceURI,
                           String sName, // simple name (localName)
                           String qName, // qualified name
                           Attributes attrs)
  throws SAXException
  {
    if (sName != null && ! sName.equals("")) 
      System.out.println("sName=" + sName);
    if (qName != null && ! qName.equals("")) 
      System.out.println("qName=" + qName);


    String xlink = attrs.getValue("xlink:href");
    if (xlink == null || xlink.equals("")) 
      return;

    System.out.println("xlink=" + xlink);
    String bibid = xlink.replaceAll("_mets.xml", "");
    System.out.println("bibid=" + bibid);

  }
}



