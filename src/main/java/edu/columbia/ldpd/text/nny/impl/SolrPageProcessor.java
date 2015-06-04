package edu.columbia.ldpd.text.nny.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.nny.NNYRecord;
import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.impl.AbstractSolrPageProcessor;
import edu.columbia.ldpd.text.impl.BasePageTransformer;

public class SolrPageProcessor extends AbstractSolrPageProcessor<NNYRecord> {
	protected static final int DEFAULT_BATCH_SIZE = 10;
    private final Configuration config;
    private final List<SolrInputDocument> solrDocs;
    private SolrServer solr;
    private int batchSize = DEFAULT_BATCH_SIZE;

    private static SolrInputDocTransformer transformer(Configuration config) {
    	return new SolrInputDocTransformer(config);
    }
    public SolrPageProcessor(Configuration config) {
        this(config,new HttpSolrServer(config.get("solrUrl")), transformer(config));
    }
    public SolrPageProcessor(Configuration config, SolrServer solr) {
    	this(config,solr,transformer(config));
    }
    public SolrPageProcessor(Configuration config, SolrServer solr,BasePageTransformer<NNYRecord, SolrInputDocument> transformer) {
    	super(transformer);
    	this.config = config;
    	this.solr = solr;
        this.solrDocs = new ArrayList<>();
    }

    public int batchSize() {
    	return this.batchSize;
    }

    public int batchSize(int batchSize) {
    	if (batchSize < 1) throw new IllegalArgumentException("Bad batchSize value \"" + batchSize + "\"");
    	this.batchSize = batchSize;
    	return this.batchSize;
    }

    @Override
    public void processPage(NNYRecord page) throws IndexingException {
        SolrInputDocument doc = toDocument(page);
    	if (config.onlyCollections() && "".equals(doc.getFieldValue(transformer.fieldName("collection",Store.YES,Multiple.NO,Tokenize.NO)))) return;
        solrDocs.add(doc);
        if (solrDocs.size() > batchSize) {
            try {
                post();
            } catch (Exception e) {
                throw new NNYException(e.getMessage(),e);
            }
        }
    }

    private void post() throws SolrServerException, IOException{
        if (solrDocs.size() == 0) return;
        
        solr.add(solrDocs);
        solr.commit();
        solrDocs.clear();
    }
    @Override
    public void cleanUp() throws NNYException {
        try {
            post();
            if (solr != null) solr.optimize();
        } catch (Exception e) {
            throw new NNYException(e.getMessage(),e);
        }
    }
}