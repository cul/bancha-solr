package edu.columbia.ldpd.text.nny;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.apache.solr.common.SolrInputDocument;

import org.junit.Before;
import org.junit.Test;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.nny.impl.SolrInputDocTransformer;
import edu.columbia.ldpd.text.impl.CollectingSolrPageProcessor;

public class BuildIndexTest {

	private Configuration testConfig;
	@Before
	public void setUp() {
		testConfig = new Configuration("test");
		testConfig.set("xmlDir","src/test/resources/nny");
		testConfig.set("homeDir", ".");
		testConfig.set("urlPrefix", "http://test.org/nny");
	}
	@Test
	public void test() {
        CollectingSolrPageProcessor<NNYRecord> proc =
                new CollectingSolrPageProcessor<>(testConfig, new SolrInputDocTransformer(testConfig));
		BuildIndex index = new BuildIndex(proc);
		try {
			index.run(testConfig, null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(53,proc.size());
		HashSet<String> ids = new HashSet<>();

		for (SolrInputDocument page: proc) {
			assertTrue(page.getFieldValue("url_ssi").toString().indexOf("//",8) == -1);
			ids.add(page.getFieldValue("id").toString());
		}
		assertEquals(53,ids.size());
	}

	@Test
	public void testFileConfig() {
		testConfig = new Configuration("test");
		testConfig.set("xmlFile","src/test/resources/nny/all.xml");
		testConfig.set("homeDir", ".");
		testConfig.set("urlPrefix", "http://test.org/nny");
		test();
	}
}
