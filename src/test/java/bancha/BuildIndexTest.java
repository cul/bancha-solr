package bancha;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.digester.Digester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import bancha.impl.CollectingPageProcessor;

@RunWith(MockitoJUnitRunner.class)
public class BuildIndexTest {

	@Mock
	private PageProcessor mockProc;

	@Mock
	Digester mockDigester;

	private BuildIndex test;

	@Before
	public void setUp() throws Exception {
		test = new BuildIndex(mockDigester);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getDigester() throws IOException, SAXException, BanchaException {
		Digester test = BuildIndex.getDigester(mockProc);
		assertNotNull(test);
	}

	@Test
	public void digesterRules() throws IOException, SAXException, BanchaException {
		Digester test = BuildIndex.getDigester(mockProc);
		test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_test_one_000_tei.xml"));
		verify(mockProc,times(1)).processPage(any(BanchaPage.class));
		//TODO this is an integration test at this point, refactor
		CollectingPageProcessor proc = new CollectingPageProcessor();
		test = BuildIndex.getDigester(proc);
		test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_test_one_000_tei.xml"));
		BanchaPage actual = proc.get(0);
		assertEquals("test_one_1",proc.idFor(actual));
		proc = new CollectingPageProcessor();
		test = BuildIndex.getDigester(proc);
		test.parse(getClass().getResourceAsStream("/bancha/xml-test/tei/ldpd_6277490_000_tei.xml"));
		assertEquals(802,proc.size());
		HashSet<String> ids = new HashSet<>(802);
		for (BanchaPage page:proc) ids.add(proc.idFor(page));
		assertEquals(802,ids.size());
	}

	@Test
	public void indexDoc() throws IOException, SAXException {
		test.indexDoc("lolwut");
		verify(mockDigester, times(1)).parse("lolwut");
	}
}
