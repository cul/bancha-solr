package edu.columbia.ldpd.text.bancha.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.impl.AbstractSolrPageProcessor;
import edu.columbia.ldpd.text.bancha.BanchaException;
import edu.columbia.ldpd.text.bancha.BanchaPage;

public class SolrPageProcessor extends AbstractSolrPageProcessor<BanchaPage> {

	protected static final int DEFAULT_BATCH_SIZE = 10;
    private final Configuration config;
    private final List<SolrInputDocument> solrDocs;
    private SolrServer solr;
    private int batchSize = DEFAULT_BATCH_SIZE;

    private static SolrInputDocTransformer transformer(Configuration config,
            Hashtable<String,String> idToCollectionHash) {
    	return new SolrInputDocTransformer(config, idToCollectionHash);
    }

    public SolrPageProcessor(Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        this(config,new HttpSolrServer(config.get("solrUrl")), transformer(config, idToCollectionHash));
    }

    public SolrPageProcessor(Configuration config,
    		SolrServer solr,
            Hashtable<String,String> idToCollectionHash) {
        this(config,solr, transformer(config, idToCollectionHash));
    }

    public SolrPageProcessor(Configuration config,
    		SolrServer solr,
    		SolrInputDocTransformer transformer) {
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
    public void processPage(BanchaPage page) throws IndexingException {
        SolrInputDocument doc = toDocument(page);
    	if (config.onlyCollections() && "".equals(doc.getFieldValue(transformer.fieldName("collection",Store.YES,Multiple.NO,Tokenize.NO)))) return;
        solrDocs.add(doc);
        if (solrDocs.size() > batchSize) {
            try {
                post();
            } catch (Exception e) {
                throw new BanchaException(e.getMessage(),e);
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
    public void cleanUp() throws BanchaException {
        try {
            post();
            if (solr != null) solr.optimize();
        } catch (Exception e) {
            throw new BanchaException(e.getMessage(),e);
        }
    }

}
