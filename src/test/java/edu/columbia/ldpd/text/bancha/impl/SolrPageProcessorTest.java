package edu.columbia.ldpd.text.bancha.impl;
import static org.junit.Assert.*;
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.columbia.ldpd.text.bancha.BanchaException;
import edu.columbia.ldpd.text.bancha.BanchaPage;
import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.impl.AbstractSolrPageProcessor;

@RunWith(MockitoJUnitRunner.class)
public class SolrPageProcessorTest {

	private static final String FIELD = "field";
	private static final String AUTHOR = "Arthur Author";
	private static final String SERIES_ID = "the id";
	private static final String DOC_ID = SERIES_ID + "_123";
	private static final String FILENAME = "ldpd_" + DOC_ID + "/pages/lol/wut.xml";
	private static final String ID = "0";
	private static final String TITLE = "The Title";
	private static final String TITLE_SORT = "Title";
	@Mock
	SolrServer mockServer;

	@Mock
	Configuration mockConfig;

	@Mock
	Hashtable<String,String> mockCollections;

	@Mock
	BanchaPage mockPage;

	private SolrPageProcessor test;

	@Before
	public void setUp() throws Exception {
		test = new SolrPageProcessor(mockConfig,mockServer,mockCollections);
		when(mockPage.getAuthor()).thenReturn(AUTHOR);
		when(mockPage.getTitle()).thenReturn(TITLE);
		when(mockPage.getTitleSort()).thenReturn(TITLE_SORT);
		when(mockPage.getTargetFileName()).thenReturn(FILENAME);
		when(mockPage.getPageId()).thenReturn(ID);
	}

	@After
	public void tearDown() throws Exception {
		test.batchSize(AbstractSolrPageProcessor.DEFAULT_BATCH_SIZE);
	}

	@Test
	public void sortable() {
        assertEquals("arthur_author",test.sortable(AUTHOR));	
	}

	@Test
	public void docIdFor() {
		assertEquals(DOC_ID,test.docIdFor(mockPage));
	}

	@Test
	public void idFor() {
		assertEquals(DOC_ID + "_" + ID,test.idFor(mockPage));
	}

	@Test
	public void fieldName() {
		assertEquals("field_ssim",test.fieldName(FIELD, Store.YES, Multiple.YES, Tokenize.NO));
		assertEquals("field_sim",test.fieldName(FIELD, Store.NO, Multiple.YES, Tokenize.NO));
		assertEquals("field_tsim",test.fieldName(FIELD, Store.YES, Multiple.YES, Tokenize.YES));
		assertEquals("field_tsi",test.fieldName(FIELD, Store.YES, Multiple.NO, Tokenize.YES));
		assertEquals("field_ti",test.fieldName(FIELD, Store.NO, Multiple.NO, Tokenize.YES));
		assertEquals("field_tim",test.fieldName(FIELD, Store.NO, Multiple.YES, Tokenize.YES));
		assertEquals("field_ssi",test.fieldName(FIELD, Store.YES, Multiple.NO, Tokenize.NO));
		assertEquals("field_si",test.fieldName(FIELD, Store.NO, Multiple.NO, Tokenize.NO));
	}

	@Test
	public void toDocument() throws IndexingException {
		SolrInputDocument actual = test.toDocument(mockPage);
		assertEquals(DOC_ID + "_" + ID, actual.getFieldValue("id"));
	}

	@Test
	public void processPage() throws IndexingException {
		test.processPage(mockPage);
	}

	@Test
	public void post() throws SolrServerException, IOException, IndexingException {
		// test indirectly, since method is private
        test.batchSize(1);
		test.processPage(mockPage);
		test.processPage(mockPage);
		verify(mockServer,times(1)).commit();
	}

	@Test
	public void cleanup() throws BanchaException, SolrServerException, IOException {
		test.cleanUp();
		verify(mockServer,times(1)).optimize();
	}

}
