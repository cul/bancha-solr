package edu.columbia.ldpd.text.bancha;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.commons.digester.Digester;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageProcessor;
import edu.columbia.ldpd.text.bancha.impl.CollectingBanchaPageProcessor;
import edu.columbia.ldpd.text.bancha.impl.CollectingSolrPageProcessor;

@RunWith(MockitoJUnitRunner.class)
public class BuildIndexTest {

	@Mock
	private PageProcessor<BanchaPage> mockProc;

	@Mock
	Digester mockDigester;

	@Mock
	Configuration mockConfig;

	@Mock
    Hashtable<String,String> mockHash;

	private BuildIndex test;

	@Before
	public void setUp() throws Exception {
		test = new BuildIndex(mockProc,mockDigester);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getDigester() throws IOException, SAXException, BanchaException {
		Digester test = IndexCallable.getDigester(mockProc);
		assertNotNull(test);
	}

	@Test
	public void digesterRules() throws IOException, SAXException, IndexingException {
		Digester test = IndexCallable.getDigester(mockProc);
		test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_test_one_000_tei.xml"));
		verify(mockProc,times(1)).processPage(any(BanchaPage.class));
		//TODO this is an integration test at this point, refactor
		CollectingBanchaPageProcessor proc = new CollectingBanchaPageProcessor();
		test = IndexCallable.getDigester(proc);
		test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_test_one_000_tei.xml"));
		BanchaPage actual = proc.get(0);
		assertEquals("test_one_1",proc.idFor(actual));
		proc = new CollectingBanchaPageProcessor();
		test = IndexCallable.getDigester(proc);
		test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_6277490_000_tei.xml"));
		assertEquals(802,proc.size());
		HashSet<String> ids = new HashSet<>(802);
		for (BanchaPage page:proc) ids.add(proc.idFor(page));
		assertEquals(802,ids.size());
	}

    @Test
    public void otherDigesterRules() throws IOException, SAXException, BanchaException {
        CollectingSolrPageProcessor proc =
                new CollectingSolrPageProcessor(mockConfig, mockHash);
        Digester test = IndexCallable.getDigester(proc);
        test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_6260645_004_tei.xml"));
        HashSet<String> ids = new HashSet<>(96);
        for (SolrInputDocument page:proc) ids.add(page.getFieldValue("id").toString());
        assertEquals(96,ids.size());
        proc =
                new CollectingSolrPageProcessor(mockConfig, mockHash);
        test = IndexCallable.getDigester(proc);
        test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_6208639_002_tei.xml"));
        assertEquals(246,proc.size());
        ids = new HashSet<>(246);
        for (SolrInputDocument page:proc) ids.add(page.getFieldValue("id").toString());
        assertEquals(246,ids.size());
    }

    @Test
	public void indexDoc() throws IOException, SAXException {
		test.indexXml("lolwut");
		verify(mockDigester, times(1)).parse("lolwut");
	}
}
