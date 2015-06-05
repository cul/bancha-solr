package edu.columbia.ldpd.text;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

	private static final String[] COLLECTIONS = {
		"/e/ldpd/projects/bancha/dnyc_mets.xml",
		"/e/ldpd/projects/bancha/corprep_mets.xml",
		"/e/ldpd/projects/bancha/columns_mets.xml"
	};

	private Configuration test;

	@Before
	public void setUp() throws Exception {
		test = new Configuration("test",getClass().getResourceAsStream("/test.properties"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void get() {
		// test configured env
		assertEquals("errata-test",test.get("errataDir"));
		// test default
		assertEquals("/e/ldpd/projects/bancha",test.get("homeDir"));
	}

	@Test
	public void set(){
	    test.set("lol","wut");
	    assertEquals("wut",test.get("lol"));
	}

	@Test
	public void absoluteXmlFile() throws IOException {
		Configuration testConfig = new Configuration("test");
		testConfig.set("xmlFile","/src/test/resources/nny/all.xml");
		testConfig.set("homeDir", ".");
	    assertEquals("/src/test/resources/nny/all.xml",testConfig.xmlFiles().iterator().next().toString());
	}
	@Test
	public void relativeXmlFile() throws IOException {
		Configuration testConfig = new Configuration("test");
		testConfig.set("xmlFile","src/test/resources/nny/all.xml");
		testConfig.set("homeDir", ".");
	    assertEquals("./src/test/resources/nny/all.xml",testConfig.xmlFiles().iterator().next().toString());
	}
	@Test
	public void collections() {
		assertArrayEquals(COLLECTIONS,test.collections());
	}

}
