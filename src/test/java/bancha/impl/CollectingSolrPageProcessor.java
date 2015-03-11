package bancha.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import bancha.BanchaException;
import bancha.BanchaPage;
import bancha.BanchaPageTransformer;
import bancha.Configuration;


public class CollectingSolrPageProcessor extends CollectingPageProcessor<SolrInputDocument> {

    protected final SolrInputDocTransformer transformer;
    public CollectingSolrPageProcessor(Configuration config,
            Hashtable<String, String> idToCollectionHash) {
        super();
        this.transformer = new SolrInputDocTransformer(config, idToCollectionHash);
    }

    @Override
    public void processPage(BanchaPage page) throws BanchaException {
        SolrInputDocument doc = toDocument(page);
        pages.add(doc);
    }

    @Override
    public void cleanUp() throws BanchaException {
    }

    public SolrInputDocument toDocument(BanchaPage page) throws BanchaException {
        return transformer.transform(page);
    }

    @Override
    public BanchaPageTransformer<SolrInputDocument> getTransformer() {
        return this.transformer;
    }

}
