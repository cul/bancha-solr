package edu.columbia.ldpd.text.bancha.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.fields.IndexTypes.Vector;
import edu.columbia.ldpd.text.bancha.BanchaException;
import edu.columbia.ldpd.text.bancha.BanchaPage;


public class CollectingSolrPageProcessor extends CollectingPageProcessor<BanchaPage,SolrInputDocument> {

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
    public PageTransformer<BanchaPage,SolrInputDocument> getTransformer() {
        return this.transformer;
    }

    @Override
    public String sortable(String src) {
        return transformer.sortable(src);
    }

	@Override
	public String docIdFor(BanchaPage page) {
		return this.transformer.docIdFor(page);
	}

	@Override
	public String fieldName(String base, Store store, Multiple multivalue,
			Tokenize tokenize) {
		return this.transformer.fieldName(base, store, multivalue, tokenize);
	}

	@Override
	public String fieldName(String base, Store store, Multiple multivalue,
			Tokenize tokenize, Vector vector) {
		return this.transformer.fieldName(base, store, multivalue, tokenize, vector);
	}

}
