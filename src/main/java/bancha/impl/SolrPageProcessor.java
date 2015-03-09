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

    private final List<SolrInputDocument> solrDocs;
    private SolrServer solr;

    public SolrPageProcessor(Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        super(config, idToCollectionHash);
        this.solrDocs = new ArrayList<>();
    }

    protected String fieldName(String base, boolean store, boolean multivalue, boolean tokenize) {
        return base + suffix(store, multivalue, tokenize);        
    }

    @Override
    public void processPage(BanchaPage page) throws BanchaException {
        SolrInputDocument doc = toDocument(page);
        solrDocs.add(doc);
        if (solrDocs.size() > 10) {
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
