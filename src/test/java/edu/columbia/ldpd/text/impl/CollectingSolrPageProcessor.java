package edu.columbia.ldpd.text.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.CollectingPageProcessor;
import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.fields.IndexTypes.Vector;
import edu.columbia.ldpd.text.impl.BasePageTransformer;


public class CollectingSolrPageProcessor<T extends TextPage> extends CollectingPageProcessor<T,SolrInputDocument> {

    protected final BasePageTransformer<T, SolrInputDocument> transformer;
    public CollectingSolrPageProcessor(Configuration config,
            BasePageTransformer<T, SolrInputDocument> transformer) {
        super();
        this.transformer = transformer;
    }

    @Override
    public void processPage(T page) throws IndexingException {
        SolrInputDocument doc = toDocument(page);
        pages.add(doc);
    }

    @Override
    public void cleanUp() throws IndexingException {
    }

    public SolrInputDocument toDocument(T page) throws IndexingException {
        return transformer.transform(page);
    }

    @Override
    public PageTransformer<T,SolrInputDocument> getTransformer() {
        return this.transformer;
    }

    @Override
    public String sortable(String src) {
        return transformer.sortable(src);
    }

	@Override
	public String docIdFor(T page) {
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
