package bancha.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import bancha.BanchaException;
import bancha.BanchaPage;
import bancha.Configuration;

public class SolrPageProcessor extends AbstractSolrPageProcessor {

	protected static final int DEFAULT_BATCH_SIZE = 10;
    private final List<SolrInputDocument> solrDocs;
    private SolrServer solr;
    private int batchSize = DEFAULT_BATCH_SIZE;

    public SolrPageProcessor(Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        super(config, idToCollectionHash);
        this.solrDocs = new ArrayList<>();
    }

    public SolrPageProcessor(Configuration config,
    		SolrServer solr,
            Hashtable<String,String> idToCollectionHash) {
        super(config, idToCollectionHash);
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
    public void processPage(BanchaPage page) throws BanchaException {
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
        if (solr == null) {
            solr = new HttpSolrServer(solrUrl);
        }
        
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
