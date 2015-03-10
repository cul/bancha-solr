package bancha.sax;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class CollectionIdHandlerTest {
    private static SAXParserFactory PARSER_FACTORY;
	private SAXParser parser;
	private Hashtable<String,String> testHash;
	private CollectionIdHandler test;

	@BeforeClass
	public static void setUpClass() throws Exception {
		PARSER_FACTORY = SAXParserFactory.newInstance();
	}

	@Before
	public void setUp() throws Exception {
		parser = PARSER_FACTORY.newSAXParser();
		testHash = new Hashtable<>();
		test = new CollectionIdHandler(null, testHash);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOne() throws SAXException, IOException {
		InputStream in = getClass().getResourceAsStream("/bancha/test_mets.xml");
		parser.parse(in, test);
		assertEquals("Test Collection",testHash.get("ldpd_6275371_000"));
	}

	@Test
	public void testMany() throws SAXException, IOException {
		InputStream in = getClass().getResourceAsStream("/bancha/corprep_mets.xml");
		parser.parse(in, test);
		assertEquals("corprep",testHash.get("ldpd_6275371_000"));
		assertEquals(56,testHash.size());
	}

}
